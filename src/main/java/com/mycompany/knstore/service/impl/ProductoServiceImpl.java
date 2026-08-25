package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Categoria;
import com.mycompany.knstore.domain.CategoriaIVA;
import com.mycompany.knstore.domain.Marca;
import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoImagen;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.domain.Subcategoria;
import com.mycompany.knstore.repository.*;
import com.mycompany.knstore.service.ProductoService;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ProductoMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Producto}.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoServiceImpl.class);

    private final ProductoRepository productoRepository;

    private final ProductoImagenRepository productoImagenRepository;

    private final ProductoPrecioRepository productoPrecioRepository;

    private final ProductoInventarioRepository productoInventarioRepository;

    private final CategoriaRepository categoriaRepository;

    private final SubcategoriaRepository subcategoriaRepository;

    private final MarcaRepository marcaRepository;

    private final CategoriaIVARepository categoriaIVARepository;

    private final ProductoMapper productoMapper;

    private final MongoTemplate mongoTemplate;

    public ProductoServiceImpl(
        ProductoRepository productoRepository,
        ProductoImagenRepository productoImagenRepository,
        ProductoPrecioRepository productoPrecioRepository,
        ProductoInventarioRepository productoInventarioRepository,
        CategoriaRepository categoriaRepository,
        SubcategoriaRepository subcategoriaRepository,
        MarcaRepository marcaRepository,
        CategoriaIVARepository categoriaIVARepository,
        ProductoMapper productoMapper,
        MongoTemplate mongoTemplate
    ) {
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
        this.productoPrecioRepository = productoPrecioRepository;
        this.productoInventarioRepository = productoInventarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.subcategoriaRepository = subcategoriaRepository;
        this.marcaRepository = marcaRepository;
        this.categoriaIVARepository = categoriaIVARepository;
        this.productoMapper = productoMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ProductoDTO save(ProductoDTO productoDTO) {
        LOG.debug("Request to save Producto : {}", productoDTO);
        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDto(producto);
    }

    @Override
    public ProductoDTO update(ProductoDTO productoDTO) {
        LOG.debug("Request to update Producto : {}", productoDTO);
        Producto producto = productoMapper.toEntity(productoDTO);
        producto = productoRepository.save(producto);
        return productoMapper.toDto(producto);
    }

    @Override
    public Optional<ProductoDTO> partialUpdate(ProductoDTO productoDTO) {
        LOG.debug("Request to partially update Producto : {}", productoDTO);

        return productoRepository
            .findById(productoDTO.getId())
            .map(existingProducto -> {
                productoMapper.partialUpdate(existingProducto, productoDTO);

                return existingProducto;
            })
            .map(productoRepository::save)
            .map(productoMapper::toDto);
    }

    @Override
    public Page<ProductoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Productos");
        return toDtoPage(productoRepository.findAll(pageable));
    }

    public Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return toDtoPage(productoRepository.findAllWithEagerRelationships(pageable));
    }

    @Override
    public Optional<ProductoDTO> findOne(String id) {
        LOG.debug("Request to get Producto : {}", id);
        return productoRepository.findOneWithEagerRelationships(id).map(this::resolverRelacionesDeUnProducto).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findBySlug(String slug) {
        LOG.debug("Request to get Producto by slug : {}", slug);
        return productoRepository.findBySlug(slug).map(this::resolverRelacionesDeUnProducto).map(productoMapper::toDto);
    }

    /**
     * Convierte una pagina de productos a DTOs resolviendo relaciones e imagenes
     * en lote (RNF-028): una sola consulta {@code findByIdIn} por repositorio.
     */
    private Page<ProductoDTO> toDtoPage(Page<Producto> page) {
        List<ProductoDTO> contenido = loadRelationships(page.getContent()).stream().map(productoMapper::toDto).toList();
        return new PageImpl<>(contenido, page.getPageable(), page.getTotalElements());
    }

    private Producto resolverRelacionesDeUnProducto(Producto producto) {
        List<Producto> resueltos = loadRelationships(List.of(producto));
        return resueltos.isEmpty() ? producto : resueltos.get(0);
    }

    /**
     * Resuelve las referencias {@code @DBRef} de una lista de productos en lote,
     * asignando en memoria las instancias completas encontradas por id (RNF-028).
     */
    private List<Producto> loadRelationships(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            return productos;
        }
        List<Producto> conId = productos
            .stream()
            .filter(producto -> producto.getId() != null)
            .toList();
        if (conId.isEmpty()) {
            return productos;
        }

        List<String> precioIds = idsDe(conId, producto -> producto.getPrecio() == null ? null : producto.getPrecio().getId());
        List<String> inventarioIds = idsDe(conId, producto -> producto.getInventario() == null ? null : producto.getInventario().getId());
        List<String> subcategoriaIds = idsDe(conId, producto ->
            producto.getSubcategoria() == null ? null : producto.getSubcategoria().getId()
        );
        List<String> marcaIds = idsDe(conId, producto -> producto.getMarca() == null ? null : producto.getMarca().getId());
        List<String> categoriaIvaIds = idsDe(conId, producto ->
            producto.getCategoriaIva() == null ? null : producto.getCategoriaIva().getId()
        );

        Map<String, ProductoPrecio> precios = buscarPorIdsEnLote(precioIds, productoPrecioRepository::findByIdIn, ProductoPrecio::getId);
        Map<String, ProductoInventario> inventarios = buscarPorIdsEnLote(
            inventarioIds,
            productoInventarioRepository::findByIdIn,
            ProductoInventario::getId
        );
        Map<String, Subcategoria> subcategorias = buscarPorIdsEnLote(
            subcategoriaIds,
            subcategoriaRepository::findByIdIn,
            Subcategoria::getId
        );
        Map<String, Marca> marcas = buscarPorIdsEnLote(marcaIds, marcaRepository::findByIdIn, Marca::getId);
        Map<String, CategoriaIVA> categoriasIva = buscarPorIdsEnLote(
            categoriaIvaIds,
            categoriaIVARepository::findByIdIn,
            CategoriaIVA::getId
        );

        // Las categorias pueden venir de la referencia directa del producto o
        // embebidas en la subcategoria resuelta: se recogen ambas y se buscan en lote.
        Set<String> categoriaIds = new HashSet<>(
            idsDe(conId, producto -> producto.getCategoria() == null ? null : producto.getCategoria().getId())
        );
        subcategorias.values().forEach(subcategoria -> {
            if (subcategoria.getCategoria() != null && subcategoria.getCategoria().getId() != null) {
                categoriaIds.add(subcategoria.getCategoria().getId());
            }
        });
        Map<String, Categoria> categorias = buscarPorIdsEnLote(categoriaIds, categoriaRepository::findByIdIn, Categoria::getId);

        for (Producto producto : conId) {
            aplicarRelacionesResueltas(producto, precios, inventarios, categorias, subcategorias, marcas, categoriasIva);
        }
        cargarImagenesEnLote(conId);
        return productos;
    }

    /**
     * Asigna en memoria las instancias resueltas por id, dejando intactas las
     * referencias no encontradas (puede tratarse de referencias historicas).
     */
    private void aplicarRelacionesResueltas(
        Producto producto,
        Map<String, ProductoPrecio> precios,
        Map<String, ProductoInventario> inventarios,
        Map<String, Categoria> categorias,
        Map<String, Subcategoria> subcategorias,
        Map<String, Marca> marcas,
        Map<String, CategoriaIVA> categoriasIva
    ) {
        if (producto.getPrecio() != null) {
            ProductoPrecio precio = precios.get(producto.getPrecio().getId());
            if (precio != null) {
                producto.setPrecio(precio);
            }
        }

        if (producto.getInventario() != null) {
            ProductoInventario inventario = inventarios.get(producto.getInventario().getId());
            if (inventario != null) {
                producto.setInventario(inventario);
            }
        }

        if (producto.getCategoria() != null) {
            Categoria categoria = categorias.get(producto.getCategoria().getId());
            if (categoria != null) {
                producto.setCategoria(categoria);
            }
        }

        if (producto.getSubcategoria() != null) {
            Subcategoria subcategoria = subcategorias.get(producto.getSubcategoria().getId());
            if (subcategoria != null) {
                if (subcategoria.getCategoria() != null && subcategoria.getCategoria().getId() != null) {
                    Categoria categoria = categorias.get(subcategoria.getCategoria().getId());
                    if (categoria != null) {
                        subcategoria.setCategoria(categoria);
                    }
                }
                producto.setSubcategoria(subcategoria);
            }
        }

        if (producto.getMarca() != null) {
            Marca marca = marcas.get(producto.getMarca().getId());
            if (marca != null) {
                producto.setMarca(marca);
            }
        }

        if (producto.getCategoriaIva() != null) {
            CategoriaIVA categoriaIva = categoriasIva.get(producto.getCategoriaIva().getId());
            if (categoriaIva != null) {
                producto.setCategoriaIva(categoriaIva);
            }
        }
    }

    /**
     * Consulta por ids en lote y los indexa por id; si la coleccion es vacia no
     * se ejecuta la consulta para evitar un {@code $in} innecesario.
     */
    private <T> Map<String, T> buscarPorIdsEnLote(
        Collection<String> ids,
        Function<Collection<String>, List<T>> finder,
        Function<T, String> obtenerId
    ) {
        if (ids == null || ids.isEmpty()) {
            return Map.of();
        }
        Map<String, T> instancias = new HashMap<>();
        for (T entidad : finder.apply(ids)) {
            String id = obtenerId.apply(entidad);
            if (id != null) {
                instancias.put(id, entidad);
            }
        }
        return instancias;
    }

    /**
     * Carga las imagenes de un lote de productos con una sola consulta
     * {@code findByProductoIdIn} y las agrupa por producto (RNF-028).
     */
    private void cargarImagenesEnLote(List<Producto> productos) {
        List<String> ids = idsDe(productos, Producto::getId);
        if (ids.isEmpty()) {
            return;
        }
        Map<String, List<ProductoImagen>> imagenesPorProducto = productoImagenRepository
            .findByProductoIdIn(ids)
            .stream()
            .filter(imagen -> imagen.getProducto() != null && imagen.getProducto().getId() != null)
            .collect(Collectors.groupingBy(imagen -> imagen.getProducto().getId()));
        for (Producto producto : productos) {
            if (producto.getId() != null) {
                producto.setImageneses(new HashSet<>(imagenesPorProducto.getOrDefault(producto.getId(), List.of())));
            }
        }
    }

    private List<String> idsDe(List<Producto> productos, Function<Producto, String> extractor) {
        return productos.stream().map(extractor).filter(Objects::nonNull).distinct().toList();
    }

    @Override
    public Page<ProductoDTO> searchActive(String query, String categoriaId, String marcaId, Pageable pageable) {
        LOG.debug("Request to search active Productos by query : {} categoriaId : {} marcaId : {}", query, categoriaId, marcaId);
        String trimmedQuery = query == null ? "" : query.trim();
        List<Criteria> clauses = new ArrayList<>();
        clauses.add(Criteria.where("activo").is(true));

        if (!trimmedQuery.isEmpty()) {
            String escapedQuery = Pattern.quote(trimmedQuery);
            // RNF-027: la busqueda hoy usa $regex (texto parcial); el indice de
            // texto creado en la migracion queda disponible para consultas nativas.
            List<Criteria> textMatches = new ArrayList<>(
                List.of(
                    Criteria.where("nombre").regex(escapedQuery, "i"),
                    Criteria.where("descripcion").regex(escapedQuery, "i"),
                    Criteria.where("sku").regex(escapedQuery, "i")
                )
            );
            // marca es @DBRef: el nombre no esta embebido en el documento producto,
            // por lo que se resuelven los ids de marcas que coinciden y se filtra por marca.$id.
            List<String> matchingMarcaIds = marcaRepository.findByNombreRegex(escapedQuery).stream().map(Marca::getId).toList();
            if (!matchingMarcaIds.isEmpty()) {
                textMatches.add(Criteria.where("marca.$id").in(matchingMarcaIds));
            }
            clauses.add(new Criteria().orOperator(textMatches));
        }

        if (StringUtils.hasText(categoriaId)) {
            clauses.add(
                new Criteria().orOperator(
                    Criteria.where("categoria.$id").is(categoriaId),
                    Criteria.where("subcategoria.$id").is(categoriaId)
                )
            );
        }

        if (StringUtils.hasText(marcaId)) {
            clauses.add(Criteria.where("marca.$id").is(marcaId));
        }

        Criteria finalCriteria = clauses.size() == 1 ? clauses.get(0) : new Criteria().andOperator(clauses);
        Query mongoQuery = Query.query(finalCriteria).with(pageable);
        List<Producto> content = mongoTemplate.find(mongoQuery, Producto.class);
        // imagenes y relaciones en lote: RF-072 ordena el resultado por el campo
        // denormalizado precio_venta al aplicar el sort del Pageable.
        cargarImagenesEnLote(content);
        return PageableExecutionUtils.getPage(content, pageable, () -> mongoTemplate.count(Query.query(finalCriteria), Producto.class)).map(
            productoMapper::toDto
        );
    }

    @Override
    public List<ProductoDTO> findAllByIds(Collection<String> ids) {
        LOG.debug("Request to get Productos by ids : {}", ids);
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Producto> productos = productoRepository.findAllById(ids);
        return loadRelationships(productos).stream().map(productoMapper::toDto).toList();
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Producto : {}", id);
        productoRepository.deleteById(id);
    }
}
