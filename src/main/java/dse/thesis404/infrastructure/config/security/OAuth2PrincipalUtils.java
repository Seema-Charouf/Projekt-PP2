package dse.thesis404.infrastructure.config.security;

import dse.thesis404.domain.model.shared.ProfileId;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.security.Principal;
import java.util.Optional;

public final class OAuth2PrincipalUtils {

    private OAuth2PrincipalUtils() {}

    
    public static Optional<Long> extractGitHubId(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        if (principal instanceof OAuth2User oauth2) {
            Object idAttr = oauth2.getAttribute("id");
            if (idAttr instanceof Number num) {
                long id = num.longValue();
                if (id > 0) {
                    return Optional.of(id);
                }
            }
            return Optional.empty();
        }

        try {
            long id = Long.parseLong(principal.getName().trim());
            if (id > 0) {
                return Optional.of(id);
            }
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return Optional.empty();
    }

    
    public static Optional<ProfileId> extractProfileId(Principal principal) {
        return extractGitHubId(principal).map(ProfileId::ofGithubUserId);
    }

    
    public static String displayName(Principal principal) {
        if (principal == null) return "nicht eingeloggt";
        if (principal instanceof OAuth2User oauth2) {
            String login = oauth2.getAttribute("login");
            if (login != null && !login.isBlank()) return login;
        }
        return principal.getName() != null ? principal.getName() : "unbekannt";
    }
}
