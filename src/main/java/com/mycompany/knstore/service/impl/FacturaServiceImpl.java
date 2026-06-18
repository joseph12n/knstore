package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Factura;
import com.mycompany.knstore.repository.FacturaRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.FacturaService;
import com.mycompany.knstore.service.dto.FacturaDTO;
import com.mycompany.knstore.service.mapper.FacturaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Factura}.
 */
@Service
public class FacturaServiceImpl implements FacturaService {

    private static final Logger LOG = LoggerFactory.getLogger(FacturaServiceImpl.class);

    private final FacturaRepository facturaRepository;

    private final FacturaMapper facturaMapper;

    public FacturaServiceImpl(FacturaRepository facturaRepository, FacturaMapper facturaMapper) {
        this.facturaRepository = facturaRepository;
        this.facturaMapper = facturaMapper;
    }

    @Override
    public FacturaDTO save(FacturaDTO facturaDTO) {
        LOG.debug("Request to save Factura : {}", facturaDTO);
        Factura factura = facturaMapper.toEntity(facturaDTO);
        factura = facturaRepository.save(factura);
        return facturaMapper.toDto(factura);
    }

    @Override
    public FacturaDTO update(FacturaDTO facturaDTO) {
        LOG.debug("Request to update Factura : {}", facturaDTO);
        Factura factura = facturaMapper.toEntity(facturaDTO);
        factura = facturaRepository.save(factura);
        return facturaMapper.toDto(factura);
    }

    @Override
    public Optional<FacturaDTO> partialUpdate(FacturaDTO facturaDTO) {
        LOG.debug("Request to partially update Factura : {}", facturaDTO);

        return facturaRepository
            .findById(facturaDTO.getId())
            .map(existingFactura -> {
                facturaMapper.partialUpdate(existingFactura, facturaDTO);

                return existingFactura;
            })
            .map(facturaRepository::save)
            .map(facturaMapper::toDto);
    }

    @Override
    public Page<FacturaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Facturas");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .map(login -> facturaRepository.findByPagoPedidoCuentaUserLogin(login, pageable).map(facturaMapper::toDto))
                .orElse(Page.empty(pageable));
        }
        return facturaRepository.findAll(pageable).map(facturaMapper::toDto);
    }

    @Override
    public Optional<FacturaDTO> findOne(String id) {
        LOG.debug("Request to get Factura : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .flatMap(login -> facturaRepository.findByIdAndPagoPedidoCuentaUserLogin(id, login))
                .map(facturaMapper::toDto);
        }
        return facturaRepository.findById(id).map(facturaMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Factura : {}", id);
        facturaRepository.deleteById(id);
    }
}
