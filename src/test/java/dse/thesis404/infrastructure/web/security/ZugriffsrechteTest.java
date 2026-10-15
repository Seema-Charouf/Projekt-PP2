package dse.thesis404.infrastructure.web.security;

import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.service.ThemeService;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.testutil.BetreuerProfilMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Zugriffsrechte – wer darf was?")
class ZugriffsrechteTest {

    private static final long BETREUER_A = 4730L;
    private static final long BETREUER_B = 4731L;

    @Autowired
    private MockMvc mvc;
    @Autowired
    private BetreuerProfilRepository betreuerProfilRepository;
    @Autowired
    private ThemeService themeService;

    private static RequestPostProcessor login(long githubId, String... rollen) {
        SimpleGrantedAuthority[] authorities = java.util.Arrays.stream(rollen)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toArray(SimpleGrantedAuthority[]::new);
        return oauth2Login().attributes(a -> a.put("id", githubId)).authorities(authorities);
    }

    private ThemeId themaVonA() {
        for (long id : new long[]{BETREUER_A, BETREUER_B}) {
            if (betreuerProfilRepository.findByGithubId(id).isEmpty()) {
                betreuerProfilRepository.save(BetreuerProfilMother.withGithubId(id));
            }
        }
        return themeService.createTheme(ProfileId.ofGithubUserId(BETREUER_A), "Thema von A", "Original");
    }

    /**
     * Zugriffsmatrix: Jede Zeile ist "Rolle – Methode – URL – erwarteter Status".
     * Neue Seiten werden hier eingetragen; so sieht man auf einen Blick, wer wohin darf.
     */
    @ParameterizedTest(name = "{0} → {1} {2} = {3}")
    @CsvSource({
            // Student (nur eingeloggt, keine Sonderrolle)
            "STUDENT,  GET,  /admin,                403",
            "STUDENT,  GET,  /addusers,             403",
            "STUDENT,  POST, /addProfile,           403",
            "STUDENT,  GET,  /betreuerProfil,       403",
            "STUDENT,  POST, /betreuerProfil/themes, 403",
            "STUDENT,  GET,  /homestudi/themen,     200",
            // Betreuer ohne Admin-Rolle
            "BETREUER, GET,  /admin,                403",
            "BETREUER, POST, /addusers,             403",
            // Admin ohne Betreuer-Rolle
            "ADMIN,    GET,  /admin,                200",
            "ADMIN,    GET,  /betreuerProfil,       403",
    })
    void zugriffsmatrix(String rolle, String methode, String url, int erwartet) throws Exception {
        String[] rollen = rolle.equals("STUDENT") ? new String[]{"USER"} : new String[]{"USER", rolle};
        var request = methode.equals("GET") ? get(url) : post(url).with(csrf());

        mvc.perform(request.with(login(BETREUER_B, rollen)))
                .andExpect(status().is(erwartet));
    }

    @Nested
    @DisplayName("IDOR: fremde Themen über erratene IDs")
    class Idor {

        @Test
        void fremdes_thema_bearbeiten_403_und_unveraendert() throws Exception {
            ThemeId id = themaVonA();

            mvc.perform(post("/betreuerProfil/editTheme/" + id)
                            .with(csrf()).with(login(BETREUER_B, "USER", "BETREUER"))
                            .sessionAttr("themeForm", new dse.thesis404.infrastructure.web.form.ThemeForm())
                            .param("titel", "Gehackt")
                            .param("beschreibungMarkdown", ""))
                    .andExpect(status().isForbidden());

            assertThat(themeService.getOwnedTheme(id, ProfileId.ofGithubUserId(BETREUER_A)).getTitel())
                    .isEqualTo("Thema von A");
        }

        @Test
        void fremdes_thema_loeschen_403_und_noch_da() throws Exception {
            ThemeId id = themaVonA();

            mvc.perform(post("/betreuerProfil/deleteTheme/" + id)
                            .with(csrf()).with(login(BETREUER_B, "USER", "BETREUER")))
                    .andExpect(status().isForbidden());

            assertThat(themeService.findByOwner(ProfileId.ofGithubUserId(BETREUER_A)))
                    .extracting(t -> t.getId()).contains(id);
        }

        @Test
        void fremde_veranstaltung_hinzufuegen_403() throws Exception {
            ThemeId id = themaVonA();

            mvc.perform(post("/betreuerProfil/editTheme/" + id + "/addVeranstaltung")
                            .with(csrf()).with(login(BETREUER_B, "USER", "BETREUER"))
                            .param("selectedFachgebiet", "Analysis"))
                    .andExpect(status().isForbidden());

            assertThat(themeService.getOwnedTheme(id, ProfileId.ofGithubUserId(BETREUER_A))
                    .getAngeforderteVeranstaltungen()).isEmpty();
        }
    }
}
