package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.infrastructure.persistence.jdbc.entity.BetreuerProfilAggregate;
import org.springframework.data.repository.CrudRepository;

/** Die profile_id IST die GitHub-ID – eine eigene Suche nach github_id ist nicht mehr nötig. */
public interface BetreuerProfilDataRepository extends CrudRepository<BetreuerProfilAggregate, Long> {
}
