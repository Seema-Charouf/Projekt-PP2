package dse.thesis404.application.port;

import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.shared.ProfileId;

import java.util.List;
import java.util.Optional;

public interface BetreuerProfilRepository {
    Optional<BetreuerProfil> findById(ProfileId id);
    Optional<BetreuerProfil> findByGithubId(long githubId);
    List<BetreuerProfil> findAll();
    void save(BetreuerProfil profil);
    void delete(ProfileId id);
    boolean exists(ProfileId id);
}
