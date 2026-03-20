package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.infrastructure.persistence.jdbc.entity.BetreuerProfilAggregate;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BetreuerProfilDataRepository extends CrudRepository<BetreuerProfilAggregate, Long> {

    @Query("SELECT * FROM betreuer_profil WHERE github_id = :githubId")
    Optional<BetreuerProfilAggregate> findByGithubId(@Param("githubId") long githubId);
}
