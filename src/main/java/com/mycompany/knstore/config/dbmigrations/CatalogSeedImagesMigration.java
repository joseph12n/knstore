package com.mycompany.knstore.config.dbmigrations;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoImagen;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Seeds real image URLs (Unsplash) for the products created by {@link CatalogSeedMigration}.
 * Only fills {@code imagenUrl} on images without bytes; manually uploaded images are left intact.
 * Idempotent: deterministic URLs matched by product slug, no duplicated documents on re-execution.
 */
@ChangeUnit(id = "catalog-seed-images-migration", order = "005")
public class CatalogSeedImagesMigration {

    private static final Map<String, String> TALLA_INICIAL_POR_CATEGORIA = Map.of(
        "Hombre",
        "38",
        "Mujer",
        "35",
        "Niño",
        "28",
        "Unisex",
        "37"
    );

    private static final String IMAGEN_TEMPLATE = "https://images.unsplash.com/photo-%s?w=800&q=80&fm=jpg&fit=crop";

    private static final List<String> UNSPLASH_PHOTO_IDS = List.of(
        "1542291026-7eec264c27ff",
        "1549298916-b41d501d3772",
        "1595950653106-6c9ebd614d3a",
        "1600185365483-26d7a4cc7519",
        "1560769629-975ec94e6a86",
        "1543508282-6319a3e2621f",
        "1525966222134-fcfa99b8ae77",
        "1606107557195-0e29a4b5b4aa",
        "1514989940723-e8e51635b782",
        "1595341888016-a392ef81b7de",
        "1584735175315-9d5df2399cd6",
        "1608256246200-53e635b5b65f",
        "1600185365926-3a2ce3cdb9eb",
        "1547949003-9792a18a2601",
        "1560343090-f0409e92791a",
        "1544005313-94ddf0286df2",
        "1607522370275-f14206abe5d3",
        "1571731956672-f2b94d7dd0cb",
        "1596703263926-eb0762ee17e4",
        "1583394838336-acd977736f90",
        "1600269452121-4f2416e55c28",
        "1608231387042-66d1773070a5",
        "1599491235138-4b88e3bb461b",
        "1590805481395-931d40b52507"
    );

    private final MongoTemplate template;

    private final Environment environment;

    public CatalogSeedImagesMigration(MongoTemplate template, Environment environment) {
        this.template = template;
        this.environment = environment;
    }

    @Execution
    public void changeSet() {
        if (!environment.getProperty("knstore.seed.catalog", Boolean.class, false)) {
            return;
        }

        for (CatalogSeedMigration.ModeloModelo modelo : CatalogSeedMigration.MODELOS) {
            for (String color : CatalogSeedMigration.COLORES) {
                ensureImagenUrl(modelo, color);
            }
        }
    }

    @RollbackExecution
    public void rollback() {}

    private void ensureImagenUrl(CatalogSeedMigration.ModeloModelo modelo, String color) {
        // Replica exacta de la construccion de slug del seed original: slugs almacenados
        // de Marca/Categoria (p.ej. "nino") y primera talla por nombre de categoria ("Niño").
        Marca marca = template.findOne(Query.query(Criteria.where("nombre").is(modelo.marca())), Marca.class);
        if (marca == null) {
            return;
        }
        Categoria categoria = template.findOne(Query.query(Criteria.where("nombre").is(modelo.categoria())), Categoria.class);
        if (categoria == null) {
            return;
        }
        String talla = TALLA_INICIAL_POR_CATEGORIA.get(modelo.categoria());
        if (talla == null) {
            return;
        }
        String slug =
            marca.getSlug() +
            "-" +
            CatalogSeedMigration.slugify(modelo.nombre()) +
            "-" +
            categoria.getSlug() +
            "-" +
            CatalogSeedMigration.slugify(color) +
            "-" +
            talla;
        Producto producto = template.findOne(Query.query(Criteria.where("slug").is(slug)), Producto.class);
        if (producto == null) {
            return;
        }

        String imagenUrl = imagenUrlPara(modelo, color);
        String imagenAlt = modelo.marca() + " " + modelo.nombre() + " color " + color.toLowerCase();
        // El $id de un @DBRef se guarda como ObjectId; sin la conversion la query no matchea.
        List<ProductoImagen> imagenes = template.find(
            Query.query(Criteria.where("producto.$id").is(new org.bson.types.ObjectId(producto.getId()))),
            ProductoImagen.class
        );

        if (imagenes.isEmpty()) {
            ProductoImagen imagen = new ProductoImagen();
            imagen.setImagenContentType("image/jpeg");
            imagen.setImagenUrl(imagenUrl);
            imagen.setImagenAlt(imagenAlt);
            imagen.setEsPrincipal(true);
            imagen.setProducto(producto);
            imagen = template.save(imagen);
            producto.setImageneses(new HashSet<>(Set.of(imagen)));
            template.save(producto);
            return;
        }

        for (ProductoImagen imagen : imagenes) {
            boolean sinBytes = imagen.getImagen() == null || imagen.getImagen().length == 0;
            if (sinBytes) {
                imagen.setImagenUrl(imagenUrl);
                imagen.setImagenAlt(imagenAlt);
                imagen.setEsPrincipal(true);
                template.save(imagen);
            }
        }
    }

    private static String imagenUrlPara(CatalogSeedMigration.ModeloModelo modelo, String color) {
        int indice =
            (CatalogSeedMigration.MODELOS.indexOf(modelo) * CatalogSeedMigration.COLORES.size() +
                CatalogSeedMigration.COLORES.indexOf(color)) % UNSPLASH_PHOTO_IDS.size();
        return IMAGEN_TEMPLATE.formatted(UNSPLASH_PHOTO_IDS.get(indice));
    }
}
