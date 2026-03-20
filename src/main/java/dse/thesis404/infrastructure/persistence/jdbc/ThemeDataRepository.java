package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.infrastructure.persistence.jdbc.entity.ThemeAggregate;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ThemeDataRepository extends CrudRepository<ThemeAggregate, UUID> {
}
