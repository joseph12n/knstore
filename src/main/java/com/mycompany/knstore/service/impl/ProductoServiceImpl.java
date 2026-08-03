package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.*;
import com.mycompany.knstore.service.ProductoService;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ProductoMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public ProductoServiceImpl(
        ProductoRepository productoRepository,
        ProductoImagenRepository productoImagenRepository,
        ProductoPrecioRepository productoPrecioRepository,
        ProductoInventarioRepository productoInventarioRepository,
        CategoriaRepository categoriaRepository,
        SubcategoriaRepository subcategoriaRepository,
        MarcaRepository marcaRepository,
        CategoriaIVARepository categoriaIVARepository,
        ProductoMapper productoMapper
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
        return productoRepository.findAll(pageable).map(this::loadRelationships).map(productoMapper::toDto);
    }

    public Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productoRepository.findAllWithEagerRelationships(pageable).map(this::loadRelationships).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findOne(String id) {
        LOG.debug("Request to get Producto : {}", id);
        return productoRepository.findOneWithEagerRelationships(id).map(this::loadRelationships).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findBySlug(String slug) {
        LOG.debug("Request to get Producto by slug : {}", slug);
        return productoRepository.findBySlug(slug).map(this::loadRelationships).map(productoMapper::toDto);
    }

    private Producto loadRelationships(Producto producto) {
        if (producto == null || producto.getId() == null) {
            return producto;
        }

        if (producto.getPrecio() != null && producto.getPrecio().getId() != null) {
            productoPrecioRepository.findById(producto.getPrecio().getId()).ifPresent(producto::setPrecio);
        }

        if (producto.getInventario() != null && producto.getInventario().getId() != null) {
            productoInventarioRepository.findById(producto.getInventario().getId()).ifPresent(producto::setInventario);
        }

        if (producto.getCategoria() != null && producto.getCategoria().getId() != null) {
            categoriaRepository.findById(producto.getCategoria().getId()).ifPresent(producto::setCategoria);
        }

        if (producto.getSubcategoria() != null && producto.getSubcategoria().getId() != null) {
            subcategoriaRepository.findById(producto.getSubcategoria().getId()).ifPresent(subcategoria -> {
                producto.setSubcategoria(subcategoria);
                if (subcategoria.getCategoria() != null && subcategoria.getCategoria().getId() != null) {
                    categoriaRepository.findById(subcategoria.getCategoria().getId()).ifPresent(subcategoria::setCategoria);
                }
            });
        }

        if (producto.getMarca() != null && producto.getMarca().getId() != null) {
            marcaRepository.findById(producto.getMarca().getId()).ifPresent(producto::setMarca);
        }

        if (producto.getCategoriaIva() != null && producto.getCategoriaIva().getId() != null) {
            categoriaIVARepository.findById(producto.getCategoriaIva().getId()).ifPresent(producto::setCategoriaIva);
        }

        return loadImages(producto);
    }

    private Producto loadImages(Producto producto) {
        if (producto != null && producto.getId() != null) {
            producto.setImageneses(new HashSet<>(productoImagenRepository.findByProductoId(producto.getId())));
        }
        return producto;
    }

    @Override
    public Page<ProductoDTO> searchActive(String query, Pageable pageable) {
        LOG.debug("Request to search active Productos by query : {}", query);
        String escapedQuery = java.util.regex.Pattern.quote(query);
        return productoRepository.searchActiveByQuery(escapedQuery, pageable).map(this::loadImages).map(productoMapper::toDto);
    }

    public Page<ProductoDTO> buscarPublico(
        String texto,
        String categoriaId,
        String subcategoriaId,
        String marcaId,
        BigDecimal precioMin,
        BigDecimal precioMax,
        Boolean destacado,
        boolean soloActivos,
        Pageable pageable
    ) {
        LOG.debug("Request to buscarPublico productos. texto: {}", texto);

        String textoNormalizado = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);

        List<Producto> filtrados = productoRepository
            .findAllWithEagerRelationships()
            .stream()
            .filter(producto -> !soloActivos || Boolean.TRUE.equals(producto.getActivo()))
            .filter(
                producto -> categoriaId == null || (producto.getCategoria() != null && categoriaId.equals(producto.getCategoria().getId()))
            )
            .filter(
                producto ->
                    subcategoriaId == null ||
                    (producto.getSubcategoria() != null && subcategoriaId.equals(producto.getSubcategoria().getId()))
            )
            .filter(producto -> marcaId == null || (producto.getMarca() != null && marcaId.equals(producto.getMarca().getId())))
            .filter(producto -> destacado == null || Objects.equals(destacado, producto.getDestacado()))
            .filter(producto -> {
                if (textoNormalizado.isBlank()) {
                    return true;
                }
                String nombre = producto.getNombre() == null ? "" : producto.getNombre().toLowerCase(Locale.ROOT);
                String descripcion = producto.getDescripcion() == null ? "" : producto.getDescripcion().toLowerCase(Locale.ROOT);
                String sku = producto.getSku() == null ? "" : producto.getSku().toLowerCase(Locale.ROOT);
                String slug = producto.getSlug() == null ? "" : producto.getSlug().toLowerCase(Locale.ROOT);
                return (
                    nombre.contains(textoNormalizado) ||
                    descripcion.contains(textoNormalizado) ||
                    sku.contains(textoNormalizado) ||
                    slug.contains(textoNormalizado)
                );
            })
            .filter(producto -> {
                BigDecimal precio =
                    producto.getPrecio() != null && producto.getPrecio().getPrecioVenta() != null
                        ? producto.getPrecio().getPrecioVenta()
                        : BigDecimal.ZERO;
                if (precioMin != null && precio.compareTo(precioMin) < 0) {
                    return false;
                }
                if (precioMax != null && precio.compareTo(precioMax) > 0) {
                    return false;
                }
                return true;
            })
            .toList();

        List<Sort.Order> orders = new ArrayList<>();
        pageable.getSort().forEach(orders::add);
        Comparator<Producto> comparator = Comparator.comparing(Producto::getNombre, String.CASE_INSENSITIVE_ORDER);

        if (!orders.isEmpty()) {
            comparator = null;
            for (Sort.Order order : orders) {
                Comparator<Producto> current = switch (order.getProperty()) {
                    case "nombre" -> Comparator.comparing(Producto::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    case "slug" -> Comparator.comparing(Producto::getSlug, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    case "sku" -> Comparator.comparing(Producto::getSku, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                    case "precio", "precioVenta" -> Comparator.comparing(
                        p -> p.getPrecio() != null ? p.getPrecio().getPrecioVenta() : null,
                        Comparator.nullsLast(BigDecimal::compareTo)
                    );
                    default -> Comparator.comparing(Producto::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                };
                if (order.isDescending()) {
                    current = current.reversed();
                }
                comparator = comparator == null ? current : comparator.thenComparing(current);
            }
        }

        filtrados = filtrados.stream().sorted(comparator).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtrados.size());
        List<ProductoDTO> content =
            start >= filtrados.size()
                ? List.of()
                : filtrados.subList(start, end).stream().map(this::loadRelationships).map(productoMapper::toDto).toList();

        return new PageImpl<>(content, pageable, filtrados.size());
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Producto : {}", id);
        productoRepository.deleteById(id);
    }
}
