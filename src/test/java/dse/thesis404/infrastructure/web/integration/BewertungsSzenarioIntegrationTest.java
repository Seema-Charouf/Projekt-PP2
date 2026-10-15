package dse.thesis404.infrastructure.web.integration;

import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.testutil.BetreuerProfilMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Integration – Bewertungs-Szenario")
class BewertungsSzenarioIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private BetreuerProfilRepository betreuerProfilRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Test
    void karla_turing_angelegt_student_filtert_compilerbau_findet_profil() throws Exception {

        BetreuerProfil karla = BetreuerProfilMother.karlaTuring();
        betreuerProfilRepository.save(karla);

        mvc.perform(get("/homestudi")
                        .param("interesse", "Compilerbau"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Karla")));
    }

    @Test
    void homestudi_oeffentlich_ohne_login() throws Exception {
        mvc.perform(get("/homestudi"))
                .andExpect(status().isOk());
    }

    @Test
    void homestudi_themen_oeffentlich() throws Exception {
        mvc.perform(get("/homestudi/themen"))
                .andExpect(status().isOk());
    }

    @Test
    void anonymer_besucher_wird_nicht_als_testuser_eingeloggt() throws Exception {
        // Früher hat ein TestAuthenticationFilter jeden anonymen Besucher still als Nutzer 1001 eingeloggt.
        // Dann wurde für 1001 ein Student angelegt und die Filter-Auswahl fremder Besucher dort gespeichert.
        mvc.perform(get("/homestudi").param("interesse", "Compilerbau"))
                .andExpect(status().isOk());

        assertThat(studentRepository.findById(ProfileId.ofGithubUserId(1001))).isEmpty();
    }
}
