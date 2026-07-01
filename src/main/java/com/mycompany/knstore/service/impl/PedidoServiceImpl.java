package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.domain.Pedido;
import com.mycompany.knstore.domain.ProductoInventario;
import com.mycompany.knstore.domain.enumeration.EstadoPedido;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.repository.ProductoInventarioRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.PedidoService;
import com.mycompany.knstore.service.dto.PedidoDTO;
import com.mycompany.knstore.service.mapper.PedidoMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Pedido}.
 */
@Service
public class PedidoServiceImpl implements PedidoService {

    private static final Logger LOG = LoggerFactory.getLogger(PedidoServiceImpl.class);

    private static final String PEDIDO_SEQUENCE_COLLECTION = "pedido_sequence";

    private final PedidoRepository pedidoRepository;

    private final CuentaRepository cuentaRepository;

    private final ItemPedidoRepository itemPedidoRepository;

    private final ProductoInventarioRepository productoInventarioRepository;

    private final MongoTemplate mongoTemplate;

    private final PedidoMapper pedidoMapper;

    public PedidoServiceImpl(
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        ItemPedidoRepository itemPedidoRepository,
        ProductoInventarioRepository productoInventarioRepository,
        MongoTemplate mongoTemplate,
        PedidoMapper pedidoMapper
    ) {
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.productoInventarioRepository = productoInventarioRepository;
        this.mongoTemplate = mongoTemplate;
        this.pedidoMapper = pedidoMapper;
    }

    @Override
    public PedidoDTO save(PedidoDTO pedidoDTO) {
        LOG.debug("Request to save Pedido : {}", pedidoDTO);
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        if (pedido.getNumeroPedido() == null || pedido.getNumeroPedido().isBlank()) {
            pedido.setNumeroPedido(generarNumeroPedido());
        }
        pedido = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public PedidoDTO update(PedidoDTO pedidoDTO) {
        LOG.debug("Request to update Pedido : {}", pedidoDTO);
        Pedido pedido = pedidoMapper.toEntity(pedidoDTO);
        pedido = pedidoRepository.save(pedido);
        return pedidoMapper.toDto(pedido);
    }

    @Override
    public Optional<PedidoDTO> partialUpdate(PedidoDTO pedidoDTO) {
        LOG.debug("Request to partially update Pedido : {}", pedidoDTO);

        return pedidoRepository
            .findById(pedidoDTO.getId())
            .map(existingPedido -> {
                EstadoPedido estadoAnterior = existingPedido.getEstado();
                pedidoMapper.partialUpdate(existingPedido, pedidoDTO);

                // Si el pedido pasa a CANCELLED, restaurar el stock de sus ítems
                if (EstadoPedido.CANCELLED.equals(existingPedido.getEstado()) && !EstadoPedido.CANCELLED.equals(estadoAnterior)) {
                    restaurarStock(existingPedido.getId());
                }

                return existingPedido;
            })
            .map(pedidoRepository::save)
            .map(pedidoMapper::toDto);
    }

    private void restaurarStock(String pedidoId) {
        List<ItemPedido> items = itemPedidoRepository.findByPedidoId(pedidoId);
        for (ItemPedido item : items) {
            if (item.getProducto() != null && item.getProducto().getInventario() != null) {
                ProductoInventario inventario = productoInventarioRepository
                    .findById(item.getProducto().getInventario().getId())
                    .orElse(null);
                if (inventario != null) {
                    inventario.setStock(inventario.getStock() + item.getCantidad());
                    productoInventarioRepository.save(inventario);
                }
            }
        }
    }

    @Override
    public Page<PedidoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pedidos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId -> pedidoRepository.findByCuentaId(cuentaId, pageable).map(pedidoMapper::toDto))
                .orElse(Page.empty(pageable));
        }
        return pedidoRepository.findAll(pageable).map(pedidoMapper::toDto);
    }

    /**
     *  Get all the pedidos where Envio is {@code null}.
     *  @return the list of entities.
     */

    public List<PedidoDTO> findAllWhereEnvioIsNull() {
        LOG.debug("Request to get all pedidos where Envio is null");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId ->
                    pedidoRepository
                        .findByCuentaIdAndEnvioIsNull(cuentaId)
                        .stream()
                        .map(pedidoMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new))
                )
                .orElseGet(LinkedList::new);
        }
        return StreamSupport.stream(pedidoRepository.findAll().spliterator(), false)
            .filter(pedido -> pedido.getEnvio() == null)
            .map(pedidoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<PedidoDTO> findOne(String id) {
        LOG.debug("Request to get Pedido : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .flatMap(cuentaId -> pedidoRepository.findByIdAndCuentaId(id, cuentaId))
                .map(pedidoMapper::toDto);
        }
        return pedidoRepository.findById(id).map(pedidoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Pedido : {}", id);
        pedidoRepository.deleteById(id);
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }

    private String generarNumeroPedido() {
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequenceKey = "PED-" + fecha;

        Query query = new Query(Criteria.where("_id").is(sequenceKey));
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true).returnNew(true);
        Document sequence = mongoTemplate.findAndModify(query, update, options, Document.class, PEDIDO_SEQUENCE_COLLECTION);

        long seq = sequence != null ? sequence.getLong("seq") : 1L;
        return String.format("PED-%s-%06d", fecha, seq);
    }
}
