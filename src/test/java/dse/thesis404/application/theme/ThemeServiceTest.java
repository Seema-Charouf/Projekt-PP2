package dse.thesis404.application.theme;

import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.application.exception.KonfliktException;
import dse.thesis404.application.exception.NichtGefundenException;
import dse.thesis404.application.exception.ZugriffVerweigertException;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.application.service.DateiUploadService;
import dse.thesis404.application.service.ThemeService;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.testutil.DateiBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit-Tests für ThemeService: Alle Abhängigkeiten sind Mocks → keine Datenbank, kein Spring, Millisekunden schnell.
 * Geprüft wird nur die Logik des Service: Besitzrechte, Versionen, Fehlerbehandlung.
 */
@DisplayName("ThemeService – Unit-Tests mit Mockito")
class ThemeServiceTest {

    private static final ProfileId BESITZER = ProfileId.ofGithubUserId(1);
    private static final ProfileId FREMDER = ProfileId.ofGithubUserId(2);

    private ThemeRepository themeRepository;
    private BetreuerProfilRepository betreuerRepository;
    private DateiUploadService upload;
    private ThemeService service;
    private Theme thema;

    @BeforeEach
    void setUp() {
        themeRepository = mock(ThemeRepository.class);
        betreuerRepository = mock(BetreuerProfilRepository.class);
        upload = mock(DateiUploadService.class);
        service = new ThemeService(themeRepository, betreuerRepository, upload);

        thema = new Theme(ThemeId.newId(), BESITZER, "Original", new Beschreibung("alt"));
        thema.versionUebernehmen(3L);
        when(themeRepository.findById(thema.getId())).thenReturn(Optional.of(thema));
    }

    @Nested
    @DisplayName("Thema anlegen")
    class Anlegen {

        @Test
        void legt_thema_mit_fachgebieten_und_veranstaltungen_an() {
            when(betreuerRepository.exists(BESITZER)).thenReturn(true);

            ThemeId id = service.createTheme(BESITZER, "Neu", "Text", Set.of("KI", " "), Set.of("Analysis"));

            assertThat(id).isNotNull();
            verify(themeRepository).save(org.mockito.ArgumentMatchers.argThat(t ->
                    t.getTitel().equals("Neu")
                            && t.getFachgebiete().size() == 1          // Leereintrag " " wird ignoriert
                            && t.getAngeforderteVeranstaltungen().size() == 1));
        }

        @Test
        void unbekannter_betreuer_kann_kein_thema_anlegen() {
            when(betreuerRepository.exists(BESITZER)).thenReturn(false);

            assertThatThrownBy(() -> service.createTheme(BESITZER, "Neu", "Text"))
                    .isInstanceOf(IllegalArgumentException.class);
            verify(themeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Besitzrechte")
    class Besitzrechte {

        @Test
        void fremder_betreuer_darf_nicht_aendern() {
            assertThatThrownBy(() -> service.updateThema(thema.getId(), FREMDER, "Hack", "", 3L))
                    .isInstanceOf(ZugriffVerweigertException.class);
            verify(themeRepository, never()).save(any());
        }

        @Test
        void fremder_betreuer_darf_nicht_loeschen() {
            assertThatThrownBy(() -> service.deleteTheme(thema.getId(), FREMDER))
                    .isInstanceOf(ZugriffVerweigertException.class);
            verify(themeRepository, never()).delete(any());
        }

        @Test
        void unbekanntes_thema_ergibt_nicht_gefunden() {
            ThemeId unbekannt = ThemeId.newId();
            when(themeRepository.findById(unbekannt)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getOwnedTheme(unbekannt, BESITZER))
                    .isInstanceOf(NichtGefundenException.class);
        }
    }

    @Nested
    @DisplayName("Bearbeiten mit Optimistic Locking")
    class Bearbeiten {

        @Test
        void aktuelle_version_wird_gespeichert() {
            service.updateThema(thema.getId(), BESITZER, "Neuer Titel", "neu", 3L);

            assertThat(thema.getTitel()).isEqualTo("Neuer Titel");
            assertThat(thema.getBeschreibung().markdown()).isEqualTo("neu");
            verify(themeRepository).save(thema);
        }

        @Test
        void veraltete_version_ergibt_konflikt_und_nichts_wird_gespeichert() {
            assertThatThrownBy(() -> service.updateThema(thema.getId(), BESITZER, "Neuer Titel", "", 2L))
                    .isInstanceOf(KonfliktException.class);
            assertThat(thema.getTitel()).isEqualTo("Original");
            verify(themeRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Datei-Upload: Aufräumen bei Fehlern")
    class Upload {

        @Test
        void schlaegt_das_speichern_fehl_wird_die_datei_wieder_geloescht() {
            Datei datei = DateiBuilder.aDatei().pdf().build();
            when(upload.storeAndCreateMeta(anyString(), any()))
                    .thenReturn(new DateiUploadService.StoredFileResult(datei, datei.storedFilename()));
            doThrow(new RuntimeException("Datenbank nicht erreichbar")).when(themeRepository).save(any());
            UploadInfoFileCommand cmd = new UploadInfoFileCommand(
                    "Bericht", null, "bericht.pdf", 3L, new ByteArrayInputStream(new byte[]{1, 2, 3}));

            assertThatThrownBy(() -> service.uploadInfoFileToTheme(thema.getId(), BESITZER, cmd))
                    .hasMessageContaining("Datenbank");
            verify(upload).deleteFromStorage(datei);   // keine verwaiste Datei im Speicher
        }
    }
}
