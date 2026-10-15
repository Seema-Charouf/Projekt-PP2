package dse.thesis404.infrastructure.web.fehler;

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
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Fehlerbehandlung – richtige Statuscodes, keine internen Details")
class FehlerbehandlungTest {

    private static final long BETREUER_A = 4720L;
    private static final long BETREUER_B = 4721L;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private BetreuerProfilRepository betreuerProfilRepository;
    @Autowired
    private ThemeService themeService;

    private void profilAnlegen(long githubId) {
        if (betreuerProfilRepository.findByGithubId(githubId).isEmpty()) {
            betreuerProfilRepository.save(BetreuerProfilMother.withGithubId(githubId));
        }
    }

    private static RequestPostProcessor alsBetreuer(long githubId) {
        return oauth2Login()
                .attributes(a -> a.put("id", githubId))
                .authorities(new SimpleGrantedAuthority("ROLE_BETREUER"));
    }

    @Test
    void fremdes_thema_ansehen_403_ohne_interne_details() throws Exception {
        profilAnlegen(BETREUER_A);
        profilAnlegen(BETREUER_B);
        ThemeId themaVonA = themeService.createTheme(ProfileId.ofGithubUserId(BETREUER_A), "Thema von A", "geheim");

        mvc.perform(get("/betreuerProfil/theme/" + themaVonA).with(alsBetreuer(BETREUER_B)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(containsString("Kein Zugriff")))
                .andExpect(content().string(not(containsString("gehört nicht"))))
                .andExpect(content().string(not(containsString("geheim"))));
    }

    @Test
    void unbekanntes_thema_404_ohne_id_im_text() throws Exception {
        profilAnlegen(BETREUER_A);
        String unbekannt = "00000000-0000-4000-8000-000000000999";

        mvc.perform(get("/betreuerProfil/theme/" + unbekannt).with(alsBetreuer(BETREUER_A)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Nicht gefunden")))
                .andExpect(content().string(not(containsString(unbekannt))));
    }

    @Test
    void login_debug_existiert_ausserhalb_von_dev_nicht() throws Exception {
        mvc.perform(get("/login-debug").with(alsBetreuer(BETREUER_A)))
                .andExpect(status().isNotFound());
    }

    @Test
    void anfrage_zu_fremdem_thema_annehmen_403() throws Exception {
        profilAnlegen(BETREUER_A);
        profilAnlegen(BETREUER_B);
        ThemeId themaVonA = themeService.createTheme(ProfileId.ofGithubUserId(BETREUER_A), "Anfrage-Thema", "");

        mvc.perform(post("/betreuerProfil/acceptTheme/99:" + themaVonA).with(csrf()).with(alsBetreuer(BETREUER_B)))
                .andExpect(status().isForbidden());
    }

    @Test
    void fremdes_dokument_herunterladen_404() throws Exception {
        profilAnlegen(BETREUER_A);

        mvc.perform(get("/betreuerProfil/documents/" + java.util.UUID.randomUUID()).with(alsBetreuer(BETREUER_A)))
                .andExpect(status().isNotFound());
    }

    @Test
    void veraltetes_formular_speichern_409_statt_ueberschreiben() throws Exception {
        profilAnlegen(BETREUER_A);
        ProfileId a = ProfileId.ofGithubUserId(BETREUER_A);
        ThemeId id = themeService.createTheme(a, "Original", "");
        Long alteVersion = themeService.getOwnedTheme(id, a).getVersion();

        // Inzwischen ändert jemand anderes (z. B. ein zweiter Tab) das Thema
        themeService.updateThema(id, a, "Aus dem anderen Tab", "", alteVersion);

        // Das alte Formular wird mit der alten Version abgeschickt
        mvc.perform(post("/betreuerProfil/editTheme/" + id)
                        .with(csrf()).with(alsBetreuer(BETREUER_A))
                        .sessionAttr("themeForm", new dse.thesis404.infrastructure.web.form.ThemeForm())
                        .param("titel", "Aus dem alten Formular")
                        .param("beschreibungMarkdown", "")
                        .param("version", String.valueOf(alteVersion)))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Gleichzeitige Änderung")));

        org.assertj.core.api.Assertions.assertThat(themeService.getOwnedTheme(id, a).getTitel())
                .isEqualTo("Aus dem anderen Tab");
    }
}
