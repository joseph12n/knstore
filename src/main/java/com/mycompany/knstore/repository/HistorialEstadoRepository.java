package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.HistorialEstado;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialEstadoRepository extends MongoRepository<HistorialEstado, String> {
    List<HistorialEstado> findByEntidadAndIdEntidadOrderByFechaDesc(String entidad, String idEntidad);
}
