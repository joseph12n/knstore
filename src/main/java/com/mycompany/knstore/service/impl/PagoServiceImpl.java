package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Pago;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.repository.PagoRepository;
import com.mycompany.knstore.repository.PedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.PagoService;
import com.mycompany.knstore.service.dto.PagoDTO;
import com.mycompany.knstore.service.mapper.PagoMapper;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Pago}.
 */
@Service
public class PagoServiceImpl implements PagoService {

    private static final Logger LOG = LoggerFactory.getLogger(PagoServiceImpl.class);

    private final PagoRepository pagoRepository;

    private final PedidoRepository pedidoRepository;

    private final CuentaRepository cuentaRepository;

    private final PagoMapper pagoMapper;

    public PagoServiceImpl(
        PagoRepository pagoRepository,
        PedidoRepository pedidoRepository,
        CuentaRepository cuentaRepository,
        PagoMapper pagoMapper
    ) {
        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.cuentaRepository = cuentaRepository;
        this.pagoMapper = pagoMapper;
    }

    @Override
    public PagoDTO save(PagoDTO pagoDTO) {
        LOG.debug("Request to save Pago : {}", pagoDTO);
        Pago pago = pagoMapper.toEntity(pagoDTO);
        pago = pagoRepository.save(pago);
        return pagoMapper.toDto(pago);
    }

    @Override
    public PagoDTO update(PagoDTO pagoDTO) {
        LOG.debug("Request to update Pago : {}", pagoDTO);
        Pago pago = pagoMapper.toEntity(pagoDTO);
        pago = pagoRepository.save(pago);
        return pagoMapper.toDto(pago);
    }

    @Override
    public Optional<PagoDTO> partialUpdate(PagoDTO pagoDTO) {
        LOG.debug("Request to partially update Pago : {}", pagoDTO);

        return pagoRepository
            .findById(pagoDTO.getId())
            .map(existingPago -> {
                pagoMapper.partialUpdate(existingPago, pagoDTO);

                return existingPago;
            })
            .map(pagoRepository::save)
            .map(pagoMapper::toDto);
    }

    @Override
    public Page<PagoDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pagos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .map(cuentaId -> {
                    LinkedList<PagoDTO> pagos = pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .flatMap(pedido -> pagoRepository.findByPedidoId(pedido.getId(), Pageable.unpaged()).getContent().stream())
                        .map(pagoMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new));
                    Page<PagoDTO> page = new PageImpl<>(pagos, pageable, pagos.size());
                    return page;
                })
                .orElse(Page.empty(pageable));
        }
        return pagoRepository.findAll(pageable).map(pagoMapper::toDto);
    }

    @Override
    public Optional<PagoDTO> findOne(String id) {
        LOG.debug("Request to get Pago : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return getCurrentAccountId()
                .flatMap(cuentaId ->
                    pedidoRepository
                        .findByCuentaId(cuentaId, Pageable.unpaged())
                        .getContent()
                        .stream()
                        .map(pedido -> pagoRepository.findByIdAndPedidoId(id, pedido.getId()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                )
                .map(pagoMapper::toDto);
        }
        return pagoRepository.findById(id).map(pagoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Pago : {}", id);
        pagoRepository.deleteById(id);
    }

    private Optional<String> getCurrentAccountId() {
        return SecurityUtils.getCurrentUserId()
            .flatMap(cuentaRepository::findOneByUserId)
            .map(cuenta -> cuenta.getId());
    }
}
