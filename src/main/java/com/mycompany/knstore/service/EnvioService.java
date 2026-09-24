package com.mycompany.knstore.service;

import com.mycompany.knstore.domain.enumeration.EstadoEnvio;
import com.mycompany.knstore.service.dto.EnvioDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.mycompany.knstore.domain.Envio}.
 */
public interface EnvioService {
    /**
     * Save a envio.
     *
     * @param envioDTO the entity to save.
     * @return the persisted entity.
     */
    EnvioDTO save(EnvioDTO envioDTO);

    /**
     * Updates a envio.
     *
     * @param envioDTO the entity to update.
     * @return the persisted entity.
     */
    EnvioDTO update(EnvioDTO envioDTO);

    /**
     * Partially updates a envio.
     *
     * @param envioDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<EnvioDTO> partialUpdate(EnvioDTO envioDTO);

    /**
     * Get all the envios.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<EnvioDTO> findAll(Pageable pageable);

    /**
     * Get the "id" envio.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<EnvioDTO> findOne(String id);

    /**
     * Asigna la transportadora y el numero de rastreo de un envio.
     *
     * @param id id del envio.
     * @param transportadora nombre de la transportadora.
     * @param numeroRastreo numero de rastreo.
     * @return el envio actualizado.
     */
    EnvioDTO asignarTracking(String id, String transportadora, String numeroRastreo);

    /**
     * Cambia el estado de un envio validando la maquina de estados (PENDING » IN_TRANSIT » DELIVERED).
     *
     * @param id id del envio.
     * @param nuevoEstado estado destino.
     * @return el envio actualizado.
     * @throws IllegalStateException si la transicion no es valida.
     */
    EnvioDTO cambiarEstado(String id, EstadoEnvio nuevoEstado);

    /**
     * Marca un envio como devuelto y deja trazabilidad cruzada con el pedido.
     *
     * @param id id del envio.
     * @return el envio actualizado.
     */
    EnvioDTO marcarDevolucion(String id);

    /**
     * Lista los envios pendientes (bandeja logistica).
     *
     * @param pageable paginacion.
     * @return pagina de envios pendientes.
     */
    Page<EnvioDTO> findAllPendientes(Pageable pageable);

    /**
     * Delete the "id" envio.
     *
     * @param id the id of the entity.
     */
    void delete(String id);
}
