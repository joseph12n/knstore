package com.mycompany.knstore.service;

import com.mycompany.knstore.domain.HistorialEstado;
import com.mycompany.knstore.repository.HistorialEstadoRepository;
import com.mycompany.knstore.security.SecurityUtils;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HistorialEstadoService {

    private final HistorialEstadoRepository historialEstadoRepository;

    public HistorialEstadoService(HistorialEstadoRepository historialEstadoRepository) {
        this.historialEstadoRepository = historialEstadoRepository;
    }

    public void registrarCambioEstado(String entidad, String idEntidad, String campo, String valorAnterior, String valorNuevo) {
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

    public List<HistorialEstado> obtenerHistorialEntidad(String entidad, String idEntidad) {
        return historialEstadoRepository.findByEntidadAndIdEntidadOrderByFechaDesc(entidad, idEntidad);
    }
}
