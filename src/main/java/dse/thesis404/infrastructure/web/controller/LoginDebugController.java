package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.infrastructure.config.security.OAuth2PrincipalUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Zeigt GitHub-ID und Rollen des eingeloggten Nutzers. Nur für die lokale Entwicklung:
 * Existiert ausschließlich im Profil "dev", in allen anderen Umgebungen gibt es /login-debug nicht (404).
 */
@Controller
@Profile("dev")
public class LoginDebugController {

    @GetMapping("/login-debug")
    public String loginDebug(Authentication auth, Model model) {
        String name = auth != null ? OAuth2PrincipalUtils.displayName(auth) : "nicht eingeloggt";
        Long githubId = auth != null ? OAuth2PrincipalUtils.extractGitHubId(auth).orElse(null) : null;
        List<String> roles = auth != null
                ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                : List.of();
        model.addAttribute("githubId", githubId);
        model.addAttribute("userName", name);
        model.addAttribute("roles", roles);
        return "login-debug";
    }
}
