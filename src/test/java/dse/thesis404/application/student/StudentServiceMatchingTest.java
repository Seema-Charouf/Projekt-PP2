package dse.thesis404.application.student;

import dse.thesis404.application.dto.ScoredTheme;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.application.port.StudentThemeRequestRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.application.service.StudentService;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("StudentService – Matching (Veranstaltung +2, Interesse +1)")
class StudentServiceMatchingTest {

    private final ThemeRepository themeRepository = mock(ThemeRepository.class);
    private final BetreuerProfilRepository betreuerRepository = mock(BetreuerProfilRepository.class);
    private StudentService service;

    private Theme thema(String titel, Set<String> veranstaltungen, Set<String> fachgebiete) {
        Theme t = new Theme(ThemeId.newId(), ProfileId.ofGithubUserId(1), titel, new Beschreibung(""));
        veranstaltungen.forEach(t::addVeranstaltung);
        fachgebiete.forEach(t::addFachgebiet);
        return t;
    }

    @BeforeEach
    void setUp() {
        when(betreuerRepository.findById(any())).thenReturn(Optional.empty());
        service = new StudentService(betreuerRepository, themeRepository,
                mock(StudentRepository.class), mock(StudentThemeRequestRepository.class));
    }

    @Test
    void schreibweise_der_eingabe_spielt_keine_rolle() {
        when(themeRepository.findAll()).thenReturn(List.of(
                thema("SE-Thema", Set.of("Softwaretechnik"), Set.of("Software Engineering"))));

        List<ScoredTheme> result = service.findMatchingThemes(
                Set.of("  softwaretechnik "), Set.of("software   ENGINEERING"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).score()).isEqualTo(3); // 2 (Veranstaltung) + 1 (Interesse)
    }

    @Test
    void ergebnisse_sind_nach_score_absteigend_sortiert() {
        Theme nurInteresse = thema("Nur Interesse", Set.of(), Set.of("Datenbanken"));
        Theme beides = thema("Beides", Set.of("Datenbanksysteme"), Set.of("Datenbanken"));
        when(themeRepository.findAll()).thenReturn(List.of(nurInteresse, beides));

        List<ScoredTheme> result = service.findMatchingThemes(Set.of("Datenbanksysteme"), Set.of("Datenbanken"));

        assertThat(result).extracting(st -> st.theme().getTitel())
                .containsExactly("Beides", "Nur Interesse");
    }

    @Test
    void ohne_eingaben_keine_treffer() {
        assertThat(service.findMatchingThemes(Set.of(" "), Set.of())).isEmpty();
    }
}
