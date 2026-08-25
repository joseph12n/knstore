package com.mycompany.knstore.repository;

import com.mycompany.knstore.domain.Marca;
import java.util.Collection;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Marca entity.
 */
@Repository
public interface MarcaRepository extends MongoRepository<Marca, String> {
    @Query("{ 'nombre': { $regex: ?0, $options: 'i' } }")
    List<Marca> findByNombreRegex(String regex);

    List<Marca> findByIdIn(Collection<String> ids);
}
