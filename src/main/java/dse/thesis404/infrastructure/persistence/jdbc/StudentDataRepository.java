package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.infrastructure.persistence.jdbc.entity.StudentAggregate;
import org.springframework.data.repository.CrudRepository;

public interface StudentDataRepository extends CrudRepository<StudentAggregate, Long> {
}
