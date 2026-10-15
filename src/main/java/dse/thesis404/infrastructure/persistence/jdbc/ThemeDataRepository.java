package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.infrastructure.persistence.jdbc.entity.ThemeAggregate;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ThemeDataRepository extends CrudRepository<ThemeAggregate, UUID> {

    /**
     * Derived Query: Spring Data JDBC erzeugt aus dem Methodennamen
     * SELECT ... FROM theme WHERE owner_profile_id = ?
     * Die Datenbank filtert – nicht Java.
     */
    List<ThemeAggregate> findByOwnerProfileId(Long ownerProfileId);
}
