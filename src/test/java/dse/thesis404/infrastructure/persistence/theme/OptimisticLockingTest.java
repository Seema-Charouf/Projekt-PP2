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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DisplayName("Optimistic Locking – gleichzeitige Änderungen werden erkannt")
class OptimisticLockingTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private BetreuerProfilRepository betreuerProfilRepository;

    private Theme neuesThema() {
        if (betreuerProfilRepository.findByGithubId(4950L).isEmpty()) {
            betreuerProfilRepository.save(BetreuerProfilMother.withGithubId(4950L));
        }
        Theme t = new Theme(ThemeId.newId(), ProfileId.ofGithubUserId(4950L), "Original", new Beschreibung(""));
        themeRepository.save(t);
        return t;
    }

    @Test
    void jede_aenderung_erhoeht_die_version() {
        Theme t = neuesThema();
        Long nachAnlegen = t.getVersion();

        t.updateTitel("Geändert");
        themeRepository.save(t);

        assertThat(t.getVersion()).isEqualTo(nachAnlegen + 1);
        assertThat(themeRepository.findById(t.getId()).orElseThrow().getVersion()).isEqualTo(nachAnlegen + 1);
    }

    @Test
    void zweite_veraltete_aenderung_wird_abgelehnt_statt_ueberschrieben() {
        Theme t = neuesThema();
        Theme tabA = themeRepository.findById(t.getId()).orElseThrow();   // Nutzerin öffnet Tab A
        Theme tabB = themeRepository.findById(t.getId()).orElseThrow();   // ... und Tab B

        tabA.updateTitel("Aus Tab A");
        themeRepository.save(tabA);                                        // klappt, Version +1

        tabB.updateTitel("Aus Tab B");
        assertThatThrownBy(() -> themeRepository.save(tabB))              // Tab B hat alte Version
                .isInstanceOf(OptimisticLockingFailureException.class);

        assertThat(themeRepository.findById(t.getId()).orElseThrow().getTitel()).isEqualTo("Aus Tab A");
    }
}
