package dse.thesis404.testutil;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;

/**
 * Simuliert einen GitHub-Login so, wie er in echt aussieht.
 *
 * Wichtig: Bei GitHub ist das Attribut "id" der Name des Nutzers (userNameAttributeName = "id").
 * Die App liest die GitHub-ID aus diesem Namen. Das Standard-oauth2Login() von Spring Security Test
 * verwendet stattdessen "sub" = "user" – damit findet die App kein Betreuerprofil und antwortet mit 403.
 */
public final class GithubLogin {

    private GithubLogin() {}

    /** @param rollen z. B. "BETREUER", "ADMIN" – "ROLE_USER" ist immer dabei */
    public static RequestPostProcessor als(long githubId, String... rollen) {
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        Arrays.stream(rollen)
                .filter(r -> !r.equals("USER"))
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .forEach(authorities::add);

        DefaultOAuth2User githubNutzer = new DefaultOAuth2User(
                authorities,
                Map.of("id", githubId, "login", "testnutzer" + githubId),
                "id");
        return oauth2Login().oauth2User(githubNutzer);
    }
}
