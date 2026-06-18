package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.ItemCarrito;
import com.mycompany.knstore.repository.ItemCarritoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.ItemCarritoService;
import com.mycompany.knstore.service.dto.ItemCarritoDTO;
import com.mycompany.knstore.service.mapper.ItemCarritoMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link com.mycompany.knstore.domain.ItemCarrito}.
 */
@Service
public class ItemCarritoServiceImpl implements ItemCarritoService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemCarritoServiceImpl.class);

    private final ItemCarritoRepository itemCarritoRepository;

    private final ItemCarritoMapper itemCarritoMapper;

    public ItemCarritoServiceImpl(ItemCarritoRepository itemCarritoRepository, ItemCarritoMapper itemCarritoMapper) {
        this.itemCarritoRepository = itemCarritoRepository;
        this.itemCarritoMapper = itemCarritoMapper;
    }

    @Override
    public ItemCarritoDTO save(ItemCarritoDTO itemCarritoDTO) {
        LOG.debug("Request to save ItemCarrito : {}", itemCarritoDTO);
        ItemCarrito itemCarrito = itemCarritoMapper.toEntity(itemCarritoDTO);
        itemCarrito = itemCarritoRepository.save(itemCarrito);
        return itemCarritoMapper.toDto(itemCarrito);
    }

    @Override
    public ItemCarritoDTO update(ItemCarritoDTO itemCarritoDTO) {
        LOG.debug("Request to update ItemCarrito : {}", itemCarritoDTO);
        ItemCarrito itemCarrito = itemCarritoMapper.toEntity(itemCarritoDTO);
        itemCarrito = itemCarritoRepository.save(itemCarrito);
        return itemCarritoMapper.toDto(itemCarrito);
    }

    @Override
    public Optional<ItemCarritoDTO> partialUpdate(ItemCarritoDTO itemCarritoDTO) {
        LOG.debug("Request to partially update ItemCarrito : {}", itemCarritoDTO);

        return itemCarritoRepository
            .findById(itemCarritoDTO.getId())
            .map(existingItemCarrito -> {
                itemCarritoMapper.partialUpdate(existingItemCarrito, itemCarritoDTO);

                return existingItemCarrito;
            })
            .map(itemCarritoRepository::save)
            .map(itemCarritoMapper::toDto);
    }

    @Override
    public List<ItemCarritoDTO> findAll() {
        LOG.debug("Request to get all ItemCarritos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .map(login ->
                    itemCarritoRepository
                        .findByCarritoCuentaUserLogin(login)
                        .stream()
                        .map(itemCarritoMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new))
                )
                .orElseGet(LinkedList::new);
        }
        return itemCarritoRepository.findAll().stream().map(itemCarritoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<ItemCarritoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return itemCarritoRepository.findAllWithEagerRelationships(pageable).map(itemCarritoMapper::toDto);
    }

    @Override
    public Optional<ItemCarritoDTO> findOne(String id) {
        LOG.debug("Request to get ItemCarrito : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserLogin()
                .flatMap(login -> itemCarritoRepository.findByIdAndCarritoCuentaUserLogin(id, login))
                .map(itemCarritoMapper::toDto);
        }
        return itemCarritoRepository.findOneWithEagerRelationships(id).map(itemCarritoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ItemCarrito : {}", id);
        itemCarritoRepository.deleteById(id);
    }
}
