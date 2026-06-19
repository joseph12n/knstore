package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.ItemPedido;
import com.mycompany.knstore.repository.ItemPedidoRepository;
import com.mycompany.knstore.security.AuthoritiesConstants;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.ItemPedidoService;
import com.mycompany.knstore.service.dto.ItemPedidoDTO;
import com.mycompany.knstore.service.mapper.ItemPedidoMapper;
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
 * Service Implementation for managing {@link com.mycompany.knstore.domain.ItemPedido}.
 */
@Service
public class ItemPedidoServiceImpl implements ItemPedidoService {

    private static final Logger LOG = LoggerFactory.getLogger(ItemPedidoServiceImpl.class);

    private final ItemPedidoRepository itemPedidoRepository;

    private final ItemPedidoMapper itemPedidoMapper;

    public ItemPedidoServiceImpl(ItemPedidoRepository itemPedidoRepository, ItemPedidoMapper itemPedidoMapper) {
        this.itemPedidoRepository = itemPedidoRepository;
        this.itemPedidoMapper = itemPedidoMapper;
    }

    @Override
    public ItemPedidoDTO save(ItemPedidoDTO itemPedidoDTO) {
        LOG.debug("Request to save ItemPedido : {}", itemPedidoDTO);
        ItemPedido itemPedido = itemPedidoMapper.toEntity(itemPedidoDTO);
        itemPedido = itemPedidoRepository.save(itemPedido);
        return itemPedidoMapper.toDto(itemPedido);
    }

    @Override
    public ItemPedidoDTO update(ItemPedidoDTO itemPedidoDTO) {
        LOG.debug("Request to update ItemPedido : {}", itemPedidoDTO);
        ItemPedido itemPedido = itemPedidoMapper.toEntity(itemPedidoDTO);
        itemPedido = itemPedidoRepository.save(itemPedido);
        return itemPedidoMapper.toDto(itemPedido);
    }

    @Override
    public Optional<ItemPedidoDTO> partialUpdate(ItemPedidoDTO itemPedidoDTO) {
        LOG.debug("Request to partially update ItemPedido : {}", itemPedidoDTO);

        return itemPedidoRepository
            .findById(itemPedidoDTO.getId())
            .map(existingItemPedido -> {
                itemPedidoMapper.partialUpdate(existingItemPedido, itemPedidoDTO);

                return existingItemPedido;
            })
            .map(itemPedidoRepository::save)
            .map(itemPedidoMapper::toDto);
    }

    @Override
    public List<ItemPedidoDTO> findAll() {
        LOG.debug("Request to get all ItemPedidos");
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserId()
                .map(login ->
                    itemPedidoRepository
                        .findByPedidoId(login)
                        .stream()
                        .map(itemPedidoMapper::toDto)
                        .collect(Collectors.toCollection(LinkedList::new))
                )
                .orElseGet(LinkedList::new);
        }
        return itemPedidoRepository.findAll().stream().map(itemPedidoMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<ItemPedidoDTO> findAllWithEagerRelationships(Pageable pageable) {
        return itemPedidoRepository.findAllWithEagerRelationships(pageable).map(itemPedidoMapper::toDto);
    }

    @Override
    public Optional<ItemPedidoDTO> findOne(String id) {
        LOG.debug("Request to get ItemPedido : {}", id);
        if (SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.CLIENTE)) {
            return SecurityUtils.getCurrentUserId()
                .flatMap(login -> itemPedidoRepository.findByIdAndPedidoId(id, login))
                .map(itemPedidoMapper::toDto);
        }
        return itemPedidoRepository.findOneWithEagerRelationships(id).map(itemPedidoMapper::toDto);
    }

    @Override
    public void delete(String id) {
        LOG.debug("Request to delete ItemPedido : {}", id);
        itemPedidoRepository.deleteById(id);
    }
}
