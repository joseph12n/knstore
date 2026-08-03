package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.repository.ProductoImagenRepository;
import com.mycompany.knstore.repository.ProductoRepository;
import com.mycompany.knstore.service.ProductoService;
import com.mycompany.knstore.service.dto.ProductoDTO;
import com.mycompany.knstore.service.mapper.ProductoMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(
        ProductoRepository productoRepository,
        ProductoImagenRepository productoImagenRepository,
        ProductoMapper productoMapper
    ) {
        this.productoRepository = productoRepository;
        this.productoImagenRepository = productoImagenRepository;
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
        return productoRepository.findAll(pageable).map(this::loadImages).map(productoMapper::toDto);
    }

    public Page<ProductoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productoRepository.findAllWithEagerRelationships(pageable).map(this::loadImages).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findOne(String id) {
        LOG.debug("Request to get Producto : {}", id);
        return productoRepository.findOneWithEagerRelationships(id).map(this::loadImages).map(productoMapper::toDto);
    }

    @Override
    public Optional<ProductoDTO> findBySlug(String slug) {
        LOG.debug("Request to get Producto by slug : {}", slug);
        return productoRepository.findBySlug(slug).map(this::loadImages).map(productoMapper::toDto);
    }

    @Override
    public Page<ProductoDTO> buscarPublico(
        String q,
        String categoriaId,
        String subcategoriaId,
        String marcaId,
        BigDecimal minPrecio,
        BigDecimal maxPrecio,
        Boolean destacado,
        boolean soloActivos,
        Pageable pageable
    ) {
        LOG.debug(
            "Request to buscar productos: q={}, categoriaId={}, subcategoriaId={}, marcaId={}, minPrecio={}, maxPrecio={}, destacado={}, soloActivos={}",
            q,
            categoriaId,
            subcategoriaId,
            marcaId,
            minPrecio,
            maxPrecio,
            destacado,
            soloActivos
        );

        String normalizedQuery = q != null ? q.trim().toLowerCase(Locale.ROOT) : null;
        List<Producto> filtrados = productoRepository
            .findAllWithEagerRelationships()
            .stream()
            .map(this::loadImages)
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
            .filter(producto -> destacado == null || destacado.equals(producto.getDestacado()))
            .filter(producto -> {
                BigDecimal precioVenta =
                    producto.getPrecio() != null && producto.getPrecio().getPrecioVenta() != null
                        ? producto.getPrecio().getPrecioVenta()
                        : BigDecimal.ZERO;
                if (minPrecio != null && precioVenta.compareTo(minPrecio) < 0) {
                    return false;
                }
                if (maxPrecio != null && precioVenta.compareTo(maxPrecio) > 0) {
                    return false;
                }
                return true;
            })
            .filter(producto -> matchesText(producto, normalizedQuery))
            .toList();

        List<Producto> sorted = applySort(filtrados, pageable.getSort());
        int total = sorted.size();
        int fromIndex = (int) pageable.getOffset();
        if (fromIndex >= total) {
            return new PageImpl<>(List.of(), pageable, total);
        }
        int toIndex = Math.min(fromIndex + pageable.getPageSize(), total);
        List<ProductoDTO> pageContent = sorted.subList(fromIndex, toIndex).stream().map(productoMapper::toDto).toList();
        return new PageImpl<>(pageContent, pageable, total);
    }

    private boolean matchesText(Producto producto, String normalizedQuery) {
        if (normalizedQuery == null || normalizedQuery.isBlank()) {
            return true;
        }
        return (
            containsIgnoreCase(producto.getNombre(), normalizedQuery) ||
            containsIgnoreCase(producto.getDescripcion(), normalizedQuery) ||
            containsIgnoreCase(producto.getSku(), normalizedQuery) ||
            containsIgnoreCase(producto.getReferencia(), normalizedQuery) ||
            containsIgnoreCase(producto.getSlug(), normalizedQuery) ||
            (producto.getMarca() != null && containsIgnoreCase(producto.getMarca().getNombre(), normalizedQuery))
        );
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private List<Producto> applySort(List<Producto> productos, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return productos;
        }
        Comparator<Producto> comparator = null;
        for (Sort.Order order : sort) {
            Comparator<Producto> next = comparatorFor(order.getProperty());
            if (next == null) {
                continue;
            }
            if (order.getDirection().isDescending()) {
                next = next.reversed();
            }
            comparator = comparator == null ? next : comparator.thenComparing(next);
        }
        if (comparator == null) {
            return productos;
        }
        List<Producto> sorted = new ArrayList<>(productos);
        sorted.sort(comparator);
        return sorted;
    }

    private Comparator<Producto> comparatorFor(String property) {
        return switch (property) {
            case "nombre" -> Comparator.comparing(p -> valueOrEmpty(p.getNombre()), String.CASE_INSENSITIVE_ORDER);
            case "slug" -> Comparator.comparing(p -> valueOrEmpty(p.getSlug()), String.CASE_INSENSITIVE_ORDER);
            case "createdDate" -> Comparator.comparing(p -> p.getCreatedDate() != null ? p.getCreatedDate() : java.time.Instant.EPOCH);
            case "precio", "precioVenta" -> Comparator.comparing(p ->
                p.getPrecio() != null && p.getPrecio().getPrecioVenta() != null ? p.getPrecio().getPrecioVenta() : BigDecimal.ZERO
            );
            default -> null;
        };
    }

    private String valueOrEmpty(String value) {
        return value != null ? value : "";
    }

    private Producto loadImages(Producto producto) {
        if (producto != null && producto.getId() != null) {
            producto.setImageneses(new HashSet<>(productoImagenRepository.findByProductoId(producto.getId())));
        }
        return producto;
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Producto : {}", id);
        productoRepository.deleteById(id);
    }
}
