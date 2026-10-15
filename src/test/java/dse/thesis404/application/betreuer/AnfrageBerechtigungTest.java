package dse.thesis404.application.betreuer;

import dse.thesis404.application.exception.NichtGefundenException;
import dse.thesis404.application.exception.ZugriffVerweigertException;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.application.port.StudentThemeRequestRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.application.service.BetreuerProfilService;
import dse.thesis404.application.service.DateiUploadService;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.domain.model.student.StudentThemeRequest;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Anfragen annehmen/ablehnen – Berechtigung liegt im Service")
class AnfrageBerechtigungTest {

    private static final ProfileId BETREUER_A = ProfileId.ofGithubUserId(1);
    private static final ProfileId BETREUER_B = ProfileId.ofGithubUserId(2);
    private static final ProfileId STUDENT = ProfileId.ofGithubUserId(99);

    private final ThemeRepository themeRepository = mock(ThemeRepository.class);
    private final StudentThemeRequestRepository requestRepository = mock(StudentThemeRequestRepository.class);
    private BetreuerProfilService service;
    private ThemeId themaVonA;

    @BeforeEach
    void setUp() {
        service = new BetreuerProfilService(
                mock(BetreuerProfilRepository.class),
                mock(DateiUploadService.class),
                themeRepository,
                requestRepository,
                mock(StudentRepository.class));

        Theme theme = new Theme(ThemeId.newId(), BETREUER_A, "Thema von A", new Beschreibung(""));
        themaVonA = theme.getId();
        when(themeRepository.findById(themaVonA)).thenReturn(Optional.of(theme));
    }

    @Test
    void eigene_existierende_anfrage_annehmen_speichert_accepted() {
        when(requestRepository.exists(STUDENT, themaVonA)).thenReturn(true);

        service.acceptRequest(BETREUER_A, STUDENT, themaVonA);

        verify(requestRepository).save(new StudentThemeRequest(STUDENT, themaVonA, RequestStatus.ACCEPTED));
    }

    @Test
    void anfrage_zu_fremdem_thema_wird_verweigert() {
        when(requestRepository.exists(STUDENT, themaVonA)).thenReturn(true);

        assertThatThrownBy(() -> service.acceptRequest(BETREUER_B, STUDENT, themaVonA))
                .isInstanceOf(ZugriffVerweigertException.class);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void erfundene_anfrage_erzeugt_keinen_datensatz() {
        when(requestRepository.exists(STUDENT, themaVonA)).thenReturn(false);

        assertThatThrownBy(() -> service.declineRequest(BETREUER_A, STUDENT, themaVonA))
                .isInstanceOf(NichtGefundenException.class);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void unbekanntes_thema_ergibt_nicht_gefunden() {
        ThemeId unbekannt = ThemeId.newId();
        when(themeRepository.findById(unbekannt)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptRequest(BETREUER_A, STUDENT, unbekannt))
                .isInstanceOf(NichtGefundenException.class);
    }
}
