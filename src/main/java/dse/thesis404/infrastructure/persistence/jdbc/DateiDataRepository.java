package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.infrastructure.persistence.jdbc.entity.DateiEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DateiDataRepository extends CrudRepository<DateiEntity, UUID> {
}
