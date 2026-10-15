package dse.thesis404.application.theme;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.Optional;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ThemeService – Thema löschen räumt auch die Dateien im Speicher auf")
class ThemeLoeschenTest {

    @Test
    void erst_datenbank_dann_alle_dateien_im_speicher() {
        ThemeRepository themeRepository = mock(ThemeRepository.class);
        DateiUploadService upload = mock(DateiUploadService.class);
        ThemeService service = new ThemeService(themeRepository, mock(BetreuerProfilRepository.class), upload);

        ProfileId owner = ProfileId.ofGithubUserId(1);
        Theme theme = new Theme(ThemeId.newId(), owner, "T", new Beschreibung(""));
        Datei pdf = DateiBuilder.aDatei().pdf().build();
        Datei zip = DateiBuilder.aDatei().zip().build();
        theme.addFile(pdf);
        theme.addFile(zip);
        when(themeRepository.findById(theme.getId())).thenReturn(Optional.of(theme));
        when(upload.deleteFromStorage(pdf)).thenReturn(true);
        when(upload.deleteFromStorage(zip)).thenReturn(true);

        service.deleteTheme(theme.getId(), owner);

        InOrder reihenfolge = inOrder(themeRepository, upload);
        reihenfolge.verify(themeRepository).delete(theme.getId());
        reihenfolge.verify(upload).deleteFromStorage(pdf);
        reihenfolge.verify(upload).deleteFromStorage(zip);
    }
}
