package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Cuenta;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.CuentaService;
import com.mycompany.knstore.service.dto.CuentaDTO;
import com.mycompany.knstore.service.mapper.CuentaMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Cuenta}.
 */
@Service
public class CuentaServiceImpl implements CuentaService {

    private static final Logger LOG = LoggerFactory.getLogger(CuentaServiceImpl.class);

    private final CuentaRepository cuentaRepository;

    private final CuentaMapper cuentaMapper;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaMapper cuentaMapper) {
        this.cuentaRepository = cuentaRepository;
        this.cuentaMapper = cuentaMapper;
    }

    @Override
    public CuentaDTO save(CuentaDTO cuentaDTO) {
        LOG.debug("Request to save Cuenta : {}", cuentaDTO);
        Cuenta cuenta = cuentaMapper.toEntity(cuentaDTO);
        cuenta = cuentaRepository.save(cuenta);
        return cuentaMapper.toDto(cuenta);
    }

    @Override
    public CuentaDTO update(CuentaDTO cuentaDTO) {
        LOG.debug("Request to update Cuenta : {}", cuentaDTO);
        Cuenta cuenta = cuentaMapper.toEntity(cuentaDTO);
        cuenta = cuentaRepository.save(cuenta);
        return cuentaMapper.toDto(cuenta);
    }

    @Override
    public Optional<CuentaDTO> partialUpdate(CuentaDTO cuentaDTO) {
        LOG.debug("Request to partially update Cuenta : {}", cuentaDTO);

        return cuentaRepository
            .findById(cuentaDTO.getId())
            .map(existingCuenta -> {
                cuentaMapper.partialUpdate(existingCuenta, cuentaDTO);

                return existingCuenta;
            })
            .map(cuentaRepository::save)
            .map(cuentaMapper::toDto);
    }

    @Override
    public Page<CuentaDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Cuentas");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .map(login -> cuentaRepository.findByUserLogin(login, pageable).map(cuentaMapper::toDto))
                .orElse(Page.empty(pageable));
        }
        return cuentaRepository.findAll(pageable).map(cuentaMapper::toDto);
    }

    public Page<CuentaDTO> findAllWithEagerRelationships(Pageable pageable) {
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .map(login -> cuentaRepository.findByUserLogin(login, pageable).map(cuentaMapper::toDto))
                .orElse(Page.empty(pageable));
        }
        return cuentaRepository.findAllWithEagerRelationships(pageable).map(cuentaMapper::toDto);
    }

    @Override
    public Optional<CuentaDTO> findOne(String id) {
        LOG.debug("Request to get Cuenta : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .flatMap(login -> cuentaRepository.findByIdAndUserLogin(id, login))
                .map(cuentaMapper::toDto);
        }
        return cuentaRepository.findOneWithEagerRelationships(id).map(cuentaMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Cuenta : {}", id);
        cuentaRepository.deleteById(id);
    }
}
