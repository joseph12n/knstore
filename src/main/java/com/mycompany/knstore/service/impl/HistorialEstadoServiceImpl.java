package com.mycompany.knstore.service.impl;

import com.mycompany.knstore.domain.HistorialEstado;
import com.mycompany.knstore.repository.HistorialEstadoRepository;
import com.mycompany.knstore.security.SecurityUtils;
import com.mycompany.knstore.service.HistorialEstadoService;
import com.mycompany.knstore.service.dto.HistorialEstadoDTO;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link HistorialEstado}.
 */
@Service
public class HistorialEstadoServiceImpl implements HistorialEstadoService {

    private static final Logger LOG = LoggerFactory.getLogger(HistorialEstadoServiceImpl.class);

    private final HistorialEstadoRepository historialEstadoRepository;

    public HistorialEstadoServiceImpl(HistorialEstadoRepository historialEstadoRepository) {
        this.historialEstadoRepository = historialEstadoRepository;
    }

    @Override
    public void registrar(String entidad, String idEntidad, String campo, String valorAnterior, String valorNuevo) {
        if (idEntidad == null || valorNuevo == null) {
            return;
        }
        LOG.debug("Registrar transicion {}[{}] {}: {} -> {}", entidad, idEntidad, campo, valorAnterior, valorNuevo);
        HistorialEstado historial = new HistorialEstado();
        historial.setEntidad(entidad);
        historial.setIdEntidad(idEntidad);
        historial.setCampo(campo);
        historial.setValorAnterior(valorAnterior);
        historial.setValorNuevo(valorNuevo);
        historial.setFecha(Instant.now());
        historial.setActor(SecurityUtils.getCurrentUserLogin().orElse("system"));
        historialEstadoRepository.save(historial);
    }

    @Override
    public List<HistorialEstadoDTO> consultar(String entidad, String idEntidad) {
        return historialEstadoRepository.findByEntidadAndIdEntidadOrderByFechaAsc(entidad, idEntidad).stream().map(this::toDto).toList();
    }

    private HistorialEstadoDTO toDto(HistorialEstado historial) {
        HistorialEstadoDTO dto = new HistorialEstadoDTO();
        dto.setId(historial.getId());
        dto.setEntidad(historial.getEntidad());
        dto.setIdEntidad(historial.getIdEntidad());
        dto.setCampo(historial.getCampo());
        dto.setValorAnterior(historial.getValorAnterior());
        dto.setValorNuevo(historial.getValorNuevo());
        dto.setFecha(historial.getFecha());
        dto.setActor(historial.getActor());
        return dto;
    }
}
