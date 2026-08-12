package com.mycompany.knstore.config.dbmigrations;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoImagen;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.domain.enumeration.EstadoIVA;
import com.mycompany.knstore.domain.enumeration.UbicacionBodega;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Seeds the catalog with demo data (IVA category, brands, categories, subcategories and
 * products) only when {@code knstore.seed.catalog} is enabled (development profile).
 * The migration is idempotent: entities are matched by slug/name and updated instead of
 * duplicated on re-execution.
 */
@ChangeUnit(id = "catalog-seed-migration", order = "003")
public class CatalogSeedMigration {

    static final List<String> COLORES = List.of("Negro", "Blanco", "Azul", "Gris");

    private final MongoTemplate template;

    private final Environment environment;

    public CatalogSeedMigration(MongoTemplate template, Environment environment) {
        this.template = template;
        this.environment = environment;
    }

    @Execution
    public void changeSet() {
        if (!environment.getProperty("knstore.seed.catalog", Boolean.class, false)) {
            return;
        }

        CategoriaIVA categoriaIva = ensureCategoriaIva();
        Map<String, Marca> marcas = seedMarcas();
        Map<String, Categoria> categorias = seedCategorias();
        Map<String, Subcategoria> subcategorias = seedSubcategorias(categorias);
        seedProductos(categoriaIva, marcas, categorias, subcategorias);
    }

    @RollbackExecution
    public void rollback() {}

    private CategoriaIVA ensureCategoriaIva() {
        CategoriaIVA categoriaIva = template.findOne(Query.query(Criteria.where("nombre").is("IVA 19%")), CategoriaIVA.class);
        if (categoriaIva == null) {
            categoriaIva = new CategoriaIVA();
            categoriaIva.setNombre("IVA 19%");
            categoriaIva.setPorcentaje(new BigDecimal("19"));
            categoriaIva.setEstado(EstadoIVA.ACTIVO);
        } else {
            categoriaIva.setPorcentaje(new BigDecimal("19"));
            categoriaIva.setEstado(EstadoIVA.ACTIVO);
        }
        return template.save(categoriaIva);
    }

    private Map<String, Marca> seedMarcas() {
        List<Marca> marcasData = List.of(createMarca("Nike", "nike"), createMarca("Adidas", "adidas"), createMarca("Puma", "puma"));
        return marcasData.stream().collect(Collectors.toMap(Marca::getNombre, this::ensureMarca));
    }

    private Marca createMarca(String nombre, String slug) {
        Marca marca = new Marca();
        marca.setNombre(nombre);
        marca.setSlug(slug);
        return marca;
    }

    private Marca ensureMarca(Marca expected) {
        Marca marca = template.findOne(Query.query(Criteria.where("slug").is(expected.getSlug())), Marca.class);
        if (marca == null) {
            marca = expected;
        } else {
            marca.setNombre(expected.getNombre());
        }
        return template.save(marca);
    }

    private Map<String, Categoria> seedCategorias() {
        List<Categoria> categoriasData = List.of(
            createCategoria("Hombre", "hombre", "Calzado hombre"),
            createCategoria("Mujer", "mujer", "Calzado mujer"),
            createCategoria("Niño", "nino", "Calzado niño"),
            createCategoria("Unisex", "unisex", "Calzado unisex")
        );
        return categoriasData.stream().collect(Collectors.toMap(Categoria::getNombre, this::ensureCategoria));
    }

    private Categoria createCategoria(String nombre, String slug, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setSlug(slug);
        categoria.setDescripcion(descripcion);
        categoria.setActivo(true);
        return categoria;
    }

    private Categoria ensureCategoria(Categoria expected) {
        Categoria categoria = template.findOne(Query.query(Criteria.where("slug").is(expected.getSlug())), Categoria.class);
        if (categoria == null) {
            categoria = expected;
        } else {
            categoria.setNombre(expected.getNombre());
            categoria.setDescripcion(expected.getDescripcion());
            categoria.setActivo(true);
        }
        return template.save(categoria);
    }

    private Map<String, Subcategoria> seedSubcategorias(Map<String, Categoria> categorias) {
        List<String> nombresSubcategorias = List.of("Deportivo", "Casual", "Formal");
        return categorias
            .values()
            .stream()
            .flatMap(categoria ->
                nombresSubcategorias
                    .stream()
                    .map(nombreSub -> Map.entry(categoria.getNombre() + "|" + nombreSub, ensureSubcategoria(categoria, nombreSub)))
            )
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Subcategoria ensureSubcategoria(Categoria categoria, String nombreSub) {
        String slugSub = slugify(nombreSub) + "-" + categoria.getSlug();
        Subcategoria subcategoria = template.findOne(Query.query(Criteria.where("slug").is(slugSub)), Subcategoria.class);
        if (subcategoria == null) {
            subcategoria = new Subcategoria();
            subcategoria.setSlug(slugSub);
        }
        subcategoria.setNombre(nombreSub + " " + categoria.getNombre());
        subcategoria.setDescripcion("Zapatos " + nombreSub.toLowerCase() + " para " + categoria.getNombre().toLowerCase());
        subcategoria.setActivo(true);
        subcategoria.setCategoria(categoria);
        return template.save(subcategoria);
    }

    private void seedProductos(
        CategoriaIVA categoriaIva,
        Map<String, Marca> marcas,
        Map<String, Categoria> categorias,
        Map<String, Subcategoria> subcategorias
    ) {
        Random random = new Random(42L);
        Map<String, List<String>> tallasPorCategoria = Map.of(
            "Hombre",
            List.of("38", "39", "40", "41", "42"),
            "Mujer",
            List.of("35", "36", "37", "38", "39"),
            "Niño",
            List.of("28", "29", "30", "31", "32"),
            "Unisex",
            List.of("37", "38", "39", "40", "41")
        );
        Map<String, PrecioModelo> preciosPorMarca = Map.of(
            "Nike",
            new PrecioModelo(new BigDecimal("320000"), new BigDecimal("180000")),
            "Adidas",
            new PrecioModelo(new BigDecimal("280000"), new BigDecimal("150000")),
            "Puma",
            new PrecioModelo(new BigDecimal("240000"), new BigDecimal("130000"))
        );

        for (ModeloModelo modelo : MODELOS) {
            Marca marca = marcas.get(modelo.marca());
            Categoria categoria = categorias.get(modelo.categoria());
            Subcategoria subcategoria = subcategorias.get(modelo.categoria() + "|" + modelo.subcategoria());
            if (marca == null || categoria == null || subcategoria == null) {
                continue;
            }

            for (String color : COLORES) {
                String talla = tallasPorCategoria.get(modelo.categoria()).get(0);
                String slug =
                    marca.getSlug() + "-" + slugify(modelo.nombre()) + "-" + categoria.getSlug() + "-" + slugify(color) + "-" + talla;
                Producto producto = template.findOne(Query.query(Criteria.where("slug").is(slug)), Producto.class);

                PrecioModelo base = preciosPorMarca.get(modelo.marca());
                BigDecimal precioVenta = base
                    .precioVenta()
                    .add(modelo.subcategoria().equals("Formal") ? new BigDecimal("50000") : BigDecimal.ZERO);
                BigDecimal precioCompra = base
                    .precioCompra()
                    .add(modelo.subcategoria().equals("Formal") ? new BigDecimal("30000") : BigDecimal.ZERO);

                if (producto == null) {
                    ProductoPrecio precio = new ProductoPrecio();
                    precio.setPrecioCompra(precioCompra);
                    precio.setPrecioVenta(precioVenta);
                    precio.setPrecioAdicional(BigDecimal.ZERO);
                    precio.setGanancia(precioVenta.subtract(precioCompra));
                    precio = template.save(precio);

                    ProductoInventario inventario = new ProductoInventario();
                    inventario.setStock(5 + random.nextInt(21));
                    inventario.setStockMinimo(3);
                    inventario.setUbicacionBodega(UbicacionBodega.BODEGA_PRINCIPAL);
                    inventario.setGarantiaMeses(6);
                    inventario = template.save(inventario);

                    producto = new Producto();
                    producto.setSlug(slug);
                    producto.setPrecio(precio);
                    producto.setInventario(inventario);
                } else if (producto.getPrecio() != null) {
                    ProductoPrecio precio = producto.getPrecio();
                    precio.setPrecioCompra(precioCompra);
                    precio.setPrecioVenta(precioVenta);
                    precio.setPrecioAdicional(BigDecimal.ZERO);
                    precio.setGanancia(precioVenta.subtract(precioCompra));
                    template.save(precio);
                }

                producto.setNombre(
                    modelo.nombre() + " " + marca.getNombre() + " " + categoria.getNombre() + " - " + color + " Talla " + talla
                );
                producto.setReferencia(modelo.referencia() + "-" + categoria.getNombre().charAt(0));
                producto.setSku(
                    modelo.referencia() +
                        "-" +
                        categoria.getNombre().charAt(0) +
                        "-" +
                        slugify(color).substring(0, 3).toUpperCase() +
                        "-" +
                        talla
                );
                producto.setColor(color);
                producto.setTalla(talla);
                producto.setCodigoBarras("77" + "%09d".formatted(100000000 + random.nextInt(899999999)));
                producto.setUnidadMedida("Par");
                producto.setDescripcion(
                    producto.getNombre() + ". Calzado " + modelo.subcategoria().toLowerCase() + " de la marca " + marca.getNombre() + "."
                );
                producto.setDestacado(modelo.subcategoria().equals("Deportivo") && color.equals("Negro"));
                producto.setActivo(true);
                producto.setCategoria(categoria);
                producto.setSubcategoria(subcategoria);
                producto.setMarca(marca);
                producto.setCategoriaIva(categoriaIva);
                boolean esNuevo = producto.getImageneses() == null || producto.getImageneses().isEmpty();
                producto = template.save(producto);

                if (esNuevo) {
                    ProductoImagen imagen = new ProductoImagen();
                    imagen.setImagenContentType("image/png");
                    imagen.setImagenAlt("placeholder");
                    imagen.setEsPrincipal(true);
                    imagen.setProducto(producto);
                    imagen = template.save(imagen);
                    producto.setImageneses(new HashSet<>(Set.of(imagen)));
                    template.save(producto);
                }
            }
        }
    }

    static String slugify(String text) {
        return java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase()
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("(^-|-$)", "");
    }

    private record PrecioModelo(BigDecimal precioVenta, BigDecimal precioCompra) {}

    record ModeloModelo(String marca, String categoria, String subcategoria, String nombre, String referencia) {}

    static final List<ModeloModelo> MODELOS = List.of(
        new ModeloModelo("Nike", "Hombre", "Deportivo", "Air Max", "NIKE-AM"),
        new ModeloModelo("Nike", "Hombre", "Casual", "Court Vision", "NIKE-CV"),
        new ModeloModelo("Nike", "Hombre", "Formal", "Oxford Classic", "NIKE-OC"),
        new ModeloModelo("Nike", "Mujer", "Deportivo", "Air Zoom", "NIKE-AZ"),
        new ModeloModelo("Nike", "Mujer", "Casual", "Tanjun", "NIKE-TJ"),
        new ModeloModelo("Nike", "Mujer", "Formal", "Ballet Flat", "NIKE-BF"),
        new ModeloModelo("Nike", "Niño", "Deportivo", "Star Runner", "NIKE-SR"),
        new ModeloModelo("Nike", "Niño", "Casual", "Pico 5", "NIKE-P5"),
        new ModeloModelo("Nike", "Unisex", "Deportivo", "Revolution", "NIKE-RV"),
        new ModeloModelo("Nike", "Unisex", "Casual", "SB Check", "NIKE-SB"),
        new ModeloModelo("Adidas", "Hombre", "Deportivo", "Ultraboost", "ADI-UB"),
        new ModeloModelo("Adidas", "Hombre", "Casual", "Grand Court", "ADI-GC"),
        new ModeloModelo("Adidas", "Hombre", "Formal", "Duramo Dress", "ADI-DD"),
        new ModeloModelo("Adidas", "Mujer", "Deportivo", "Galaxy", "ADI-GL"),
        new ModeloModelo("Adidas", "Mujer", "Casual", "Advantage", "ADI-AV"),
        new ModeloModelo("Adidas", "Mujer", "Formal", "Court Silk", "ADI-CS"),
        new ModeloModelo("Adidas", "Niño", "Deportivo", "Runfalcon", "ADI-RF"),
        new ModeloModelo("Adidas", "Niño", "Casual", "Tensaur", "ADI-TS"),
        new ModeloModelo("Adidas", "Unisex", "Deportivo", "Lite Racer", "ADI-LR"),
        new ModeloModelo("Adidas", "Unisex", "Casual", "VL Court", "ADI-VC"),
        new ModeloModelo("Puma", "Hombre", "Deportivo", "RS-X", "PMA-RS"),
        new ModeloModelo("Puma", "Hombre", "Casual", "Smash V2", "PMA-SV"),
        new ModeloModelo("Puma", "Hombre", "Formal", "Dress Soft", "PMA-DS"),
        new ModeloModelo("Puma", "Mujer", "Deportivo", "Deviate", "PMA-DV"),
        new ModeloModelo("Puma", "Mujer", "Casual", "Carina", "PMA-CA"),
        new ModeloModelo("Puma", "Mujer", "Formal", "Ella Loafer", "PMA-EL"),
        new ModeloModelo("Puma", "Niño", "Deportivo", "Future Rider", "PMA-FR"),
        new ModeloModelo("Puma", "Niño", "Casual", "Puma Fun", "PMA-PF"),
        new ModeloModelo("Puma", "Unisex", "Deportivo", "Speedcat", "PMA-SC"),
        new ModeloModelo("Puma", "Unisex", "Casual", "Suede Classic", "PMA-SC2")
    );
}
