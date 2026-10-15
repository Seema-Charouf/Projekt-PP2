package dse.thesis404.infrastructure.web.support;

import dse.thesis404.application.exception.ZugriffVerweigertException;
import dse.thesis404.application.service.BetreuerProfilService;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.infrastructure.config.security.OAuth2PrincipalUtils;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Ermittelt das Betreuerprofil des eingeloggten Nutzers – ausschließlich aus dem echten Login (GitHub-ID).
 * Wird von allen Betreuer-Controllern geteilt, statt die Logik in jedem Controller zu wiederholen.
 */
@Component
public class AktuellerBetreuer {

    private final BetreuerProfilService betreuerProfilService;

    public AktuellerBetreuer(BetreuerProfilService betreuerProfilService) {
        this.betreuerProfilService = betreuerProfilService;
    }

    public ProfileId profileId(Principal principal) {
        if (principal == null) {
            throw new ZugriffVerweigertException("Nicht authentifiziert.");
        }
        Long githubId = OAuth2PrincipalUtils.extractGitHubId(principal)
                .orElseThrow(() -> new ZugriffVerweigertException("Keine gültige GitHub-ID."));
        return betreuerProfilService.findByGithubId(githubId)
                .map(BetreuerProfil::getId)
                .orElseThrow(() -> new ZugriffVerweigertException(
                        "Kein Betreuerprofil für GitHub-ID " + githubId));
    }
}
