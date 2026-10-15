package dse.thesis404.infrastructure.web.xss;

import dse.thesis404.testutil.GithubLogin;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.service.ThemeService;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.testutil.BetreuerProfilMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prüft mit echten Angriffs-Strings, dass Nutzereingaben in der Seite als Text
 * und nicht als HTML landen (Schutz vor Cross-Site Scripting).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("XSS – Nutzereingaben werden escaped angezeigt")
class XssIntegrationTest {

    private static final long GITHUB_ID = 4711L;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private BetreuerProfilRepository betreuerProfilRepository;
    @Autowired
    private ThemeService themeService;

    private ThemeId themaMitBeschreibung(String beschreibung) {
        if (betreuerProfilRepository.findByGithubId(GITHUB_ID).isEmpty()) {
            betreuerProfilRepository.save(BetreuerProfilMother.withGithubId(GITHUB_ID));
        }
        return themeService.createTheme(ProfileId.ofGithubUserId(GITHUB_ID), "XSS-Test", beschreibung);
    }

    private org.springframework.test.web.servlet.ResultActions themaSeiteAufrufen(ThemeId id) throws Exception {
        return mvc.perform(get("/betreuerProfil/theme/" + id).with(GithubLogin.als(GITHUB_ID, "BETREUER")));
    }

    @Test
    void img_onerror_wird_als_text_angezeigt() throws Exception {
        ThemeId id = themaMitBeschreibung("<img src=x onerror=alert(1)>");

        themaSeiteAufrufen(id)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;img src=x onerror=alert(1)&gt;")))
                .andExpect(content().string(not(containsString("<img src=x onerror"))));
    }

    @Test
    void svg_onload_wird_als_text_angezeigt() throws Exception {
        ThemeId id = themaMitBeschreibung("<svg onload=alert(1)>");

        themaSeiteAufrufen(id)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;svg onload=alert(1)&gt;")))
                .andExpect(content().string(not(containsString("<svg onload"))));
    }

    @Test
    void harmloser_text_ueber_script_tags_ist_erlaubt_und_wird_escaped() throws Exception {
        // Früher hat ein Regex-Filter das abgelehnt, obwohl es beim Anzeigen ohnehin escaped wird.
        ThemeId id = themaMitBeschreibung("Tutorial: So nutzt man <script> in HTML");

        themaSeiteAufrufen(id)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("So nutzt man &lt;script&gt; in HTML")));
    }
}
