package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.Carrito;
import com.mycompany.knstore.repository.CarritoRepository;
import com.mycompany.knstore.repository.CuentaRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.CarritoService;
import com.mycompany.knstore.service.dto.CarritoDTO;
import com.mycompany.knstore.service.mapper.CarritoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.Carrito}.
 */
@Service
public class CarritoServiceImpl implements CarritoService {

    private static final Logger LOG = LoggerFactory.getLogger(CarritoServiceImpl.class);

    private final CarritoRepository carritoRepository;

    private final CuentaRepository cuentaRepository;

    private final CarritoMapper carritoMapper;

    public CarritoServiceImpl(CarritoRepository carritoRepository, CuentaRepository cuentaRepository, CarritoMapper carritoMapper) {
        this.carritoRepository = carritoRepository;
        this.cuentaRepository = cuentaRepository;
        this.carritoMapper = carritoMapper;
    }

    @Override
    public CarritoDTO save(CarritoDTO carritoDTO) {
        LOG.debug("Request to save Carrito : {}", carritoDTO);
        Carrito carrito = carritoMapper.toEntity(carritoDTO);
        carrito = carritoRepository.save(carrito);
        return carritoMapper.toDto(carrito);
    }

    @Override
    public CarritoDTO update(CarritoDTO carritoDTO) {
        LOG.debug("Request to update Carrito : {}", carritoDTO);
        Carrito carrito = carritoMapper.toEntity(carritoDTO);
        carrito = carritoRepository.save(carrito);
        return carritoMapper.toDto(carrito);
    }

    @Override
    public Optional<CarritoDTO> partialUpdate(CarritoDTO carritoDTO) {
        LOG.debug("Request to partially update Carrito : {}", carritoDTO);

        return carritoRepository
            .findById(carritoDTO.getId())
            .map(existingCarrito -> {
                carritoMapper.partialUpdate(existingCarrito, carritoDTO);

                return existingCarrito;
            })
            .map(carritoRepository::save)
            .map(carritoMapper::toDto);
    }

    @Override
    public List<CarritoDTO> findAll() {
        LOG.debug("Request to get all Carritos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserId()
                .flatMap(cuentaRepository::findOneByUserId)
                .map(cuenta -> carritoRepository.findByCuentaId(cuenta.getId()))
                .map(carritos -> carritos.stream().map(carritoMapper::toDto).collect(Collectors.toCollection(LinkedList::new)))
                .orElseGet(LinkedList::new);
        }
        return carritoRepository.findAll().stream().map(carritoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public Optional<CarritoDTO> findOne(String id) {
        LOG.debug("Request to get Carrito : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserId()
                .flatMap(cuentaRepository::findOneByUserId)
                .flatMap(cuenta -> carritoRepository.findByIdAndCuentaId(id, cuenta.getId()))
                .map(carritoMapper::toDto);
        }
        return carritoRepository.findById(id).map(carritoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete Carrito : {}", id);
        carritoRepository.deleteById(id);
    }
}
