package dse.thesis404.infrastructure.persistence.theme;

import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.testutil.BetreuerProfilMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DisplayName("ThemeRepository – Themen pro Betreuer per Datenbank-Abfrage")
class ThemeRepositoryTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private BetreuerProfilRepository betreuerProfilRepository;

    private ProfileId betreuer(long githubId) {
        if (betreuerProfilRepository.findByGithubId(githubId).isEmpty()) {
            betreuerProfilRepository.save(BetreuerProfilMother.withGithubId(githubId));
        }
        return ProfileId.ofGithubUserId(githubId);
    }

    private Theme speichere(ProfileId owner, String titel) {
        Theme t = new Theme(ThemeId.newId(), owner, titel, new Beschreibung(""));
        t.addFachgebiet("Datenbanken");
        themeRepository.save(t);
        return t;
    }

    @Test
    void liefert_nur_die_themen_des_betreuers_samt_kindern() {
        ProfileId a = betreuer(4800);
        ProfileId b = betreuer(4801);
        speichere(a, "A1");
        speichere(a, "A2");
        speichere(b, "B1");

        List<Theme> themenVonA = themeRepository.findByOwnerProfileId(a);

        assertThat(themenVonA).extracting(Theme::getTitel).containsExactlyInAnyOrder("A1", "A2");
        assertThat(themenVonA).allSatisfy(t -> {
            assertThat(t.getOwnerProfileId()).isEqualTo(a);
            assertThat(t.getFachgebiete()).isNotEmpty();   // Kind-Tabellen werden mitgeladen
        });
    }

    @Test
    void betreuer_ohne_themen_bekommt_leere_liste() {
        assertThat(themeRepository.findByOwnerProfileId(betreuer(4802))).isEmpty();
    }
}
