package dse.thesis404.application.port;

import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;

import java.util.List;
import java.util.Optional;

public interface ThemeRepository {
    Optional<Theme> findById(ThemeId id);
    List<Theme> findAll();
    List<Theme> findByOwnerProfileId(ProfileId ownerProfileId);
    void save(Theme theme);
    void delete(ThemeId id);
    boolean exists(ThemeId id);
}
