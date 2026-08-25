package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Producto;
import com.mycompany.knstore.domain.ProductoPrecio;
import com.mycompany.knstore.repository.ProductoPrecioRepository;
import com.mycompany.knstore.service.ProductoPrecioService;
import com.mycompany.knstore.service.dto.ProductoPrecioDTO;
import com.mycompany.knstore.service.mapper.ProductoPrecioMapper;
import com.mycompany.knstore.service.util.MoneyUtils;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.ProductoPrecio}.
 */
@Service
public class ProductoPrecioServiceImpl implements ProductoPrecioService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductoPrecioServiceImpl.class);

    private final ProductoPrecioRepository productoPrecioRepository;

    private final ProductoPrecioMapper productoPrecioMapper;

    private final MongoTemplate mongoTemplate;

    public ProductoPrecioServiceImpl(
        ProductoPrecioRepository productoPrecioRepository,
        ProductoPrecioMapper productoPrecioMapper,
        MongoTemplate mongoTemplate
    ) {
        this.productoPrecioRepository = productoPrecioRepository;
        this.productoPrecioMapper = productoPrecioMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ProductoPrecioDTO save(ProductoPrecioDTO productoPrecioDTO) {
        LOG.debug("Request to save ProductoPrecio : {}", productoPrecioDTO);
        ProductoPrecio productoPrecio = productoPrecioMapper.toEntity(productoPrecioDTO);
        productoPrecio = guardarConTotales(productoPrecio);
        return productoPrecioMapper.toDto(productoPrecio);
    }

    @Override
    public ProductoPrecioDTO update(ProductoPrecioDTO productoPrecioDTO) {
        LOG.debug("Request to update ProductoPrecio : {}", productoPrecioDTO);
        ProductoPrecio productoPrecio = productoPrecioMapper.toEntity(productoPrecioDTO);
        productoPrecio = guardarConTotales(productoPrecio);
        return productoPrecioMapper.toDto(productoPrecio);
    }

    @Override
    public Optional<ProductoPrecioDTO> partialUpdate(ProductoPrecioDTO productoPrecioDTO) {
        LOG.debug("Request to partially update ProductoPrecio : {}", productoPrecioDTO);

        return productoPrecioRepository
            .findById(productoPrecioDTO.getId())
            .map(existingProductoPrecio -> {
                productoPrecioMapper.partialUpdate(existingProductoPrecio, productoPrecioDTO);
                return guardarConTotales(existingProductoPrecio);
            })
            .map(productoPrecioMapper::toDto);
    }

    private ProductoPrecio guardarConTotales(ProductoPrecio productoPrecio) {
        productoPrecio.setPrecioCompra(MoneyUtils.normalizar(productoPrecio.getPrecioCompra()));
        productoPrecio.setPrecioVenta(MoneyUtils.normalizar(productoPrecio.getPrecioVenta()));
        productoPrecio.setPrecioAdicional(MoneyUtils.normalizar(productoPrecio.getPrecioAdicional()));
        calcularGanancia(productoPrecio);
        ProductoPrecio guardado = productoPrecioRepository.save(productoPrecio);
        sincronizarPrecioVentaEnProducto(guardado);
        return guardado;
    }

    /**
     * RF-072: mantiene el campo denormalizado {@code producto.precio_venta}
     * sincronizado con el precio de venta del producto_precio para permitir
     * ordenamiento server-side por precio (el DBRef no vive en el documento
     * producto). Solo actua si el precio tiene un producto asociado; de lo
     * contrario es no-op (la migracion realiza el backfill inicial).
     */
    private void sincronizarPrecioVentaEnProducto(ProductoPrecio productoPrecio) {
        if (
            productoPrecio.getProducto() == null || productoPrecio.getProducto().getId() == null || productoPrecio.getPrecioVenta() == null
        ) {
            return;
        }
        BigDecimal valorNormalizado = MoneyUtils.normalizar(productoPrecio.getPrecioVenta());
        mongoTemplate.updateFirst(
            new Query(Criteria.where("_id").is(productoPrecio.getProducto().getId())),
            new Update().set("precio_venta", valorNormalizado),
            Producto.class
        );
    }

    private void calcularGanancia(ProductoPrecio productoPrecio) {
        BigDecimal precioVenta = productoPrecio.getPrecioVenta();
        BigDecimal precioCompra = productoPrecio.getPrecioCompra();
        if (precioVenta != null && precioCompra != null) {
            productoPrecio.setGanancia(MoneyUtils.normalizar(precioVenta.subtract(precioCompra)));
        }
    }

    @Override
    public List<ProductoPrecioDTO> findAll() {
        LOG.debug("Request to get all ProductoPrecios");
        return productoPrecioRepository
            .findAll()
            .stream()
            .map(productoPrecioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get all the productoPrecios where Producto is {@code null}.
     *  @return the list of entities.
     */

    public List<ProductoPrecioDTO> findAllWhereProductoIsNull() {
        LOG.debug("Request to get all productoPrecios where Producto is null");
        return StreamSupport.stream(productoPrecioRepository.findAll().spliterator(), false)
            .filter(productoPrecio -> productoPrecio.getProducto() == null)
            .map(productoPrecioMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<ProductoPrecioDTO> findOne(String id) {
        LOG.debug("Request to get ProductoPrecio : {}", id);
        return productoPrecioRepository.findById(id).map(productoPrecioMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ProductoPrecio : {}", id);
        productoPrecioRepository.deleteById(id);
    }
}
