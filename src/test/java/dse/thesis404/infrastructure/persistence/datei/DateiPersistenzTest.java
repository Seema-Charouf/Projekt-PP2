package dse.thesis404.infrastructure.persistence.datei;

import dse.thesis404.testutil.PersistenzTest;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.infrastructure.persistence.jdbc.DateiDataRepository;
import dse.thesis404.testutil.BetreuerProfilMother;
import dse.thesis404.testutil.DateiBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@PersistenzTest
@DisplayName("Datei-Metadaten – speichern ohne Fehler, löschen ohne Waisen")
class DateiPersistenzTest {

    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private BetreuerProfilRepository betreuerProfilRepository;
    @Autowired
    private DateiDataRepository dateiDataRepository;

    private ProfileId betreuer(long githubId) {
        if (betreuerProfilRepository.findByGithubId(githubId).isEmpty()) {
            betreuerProfilRepository.save(BetreuerProfilMother.withGithubId(githubId));
        }
        return ProfileId.ofGithubUserId(githubId);
    }

    private Theme themaMitDatei(ProfileId owner, Datei datei) {
        Theme t = new Theme(ThemeId.newId(), owner, "Mit Datei", new Beschreibung(""));
        t.addFile(datei);
        themeRepository.save(t);
        return t;
    }

    private boolean inDb(Datei d) {
        return dateiDataRepository.existsById(d.id().value());
    }

    @Test
    void neue_datei_wird_gespeichert() {
        // Vorher: UPDATE statt INSERT → Exception, Upload unmöglich
        Datei datei = DateiBuilder.aDatei().pdf().build();
        Theme t = themaMitDatei(betreuer(4900), datei);

        assertThat(inDb(datei)).isTrue();
        assertThat(themeRepository.findById(t.getId()).orElseThrow().getFiles()).containsExactly(datei);
    }

    @Test
    void erneutes_speichern_aendert_nichts_an_den_dateien() {
        Datei datei = DateiBuilder.aDatei().pdf().build();
        Theme t = themaMitDatei(betreuer(4900), datei);

        t.updateTitel("Neuer Titel");
        themeRepository.save(t);

        assertThat(themeRepository.findById(t.getId()).orElseThrow().getFiles()).containsExactly(datei);
    }

    @Test
    void entfernte_datei_hinterlaesst_keine_waise() {
        Datei datei = DateiBuilder.aDatei().pdf().build();
        Theme t = themaMitDatei(betreuer(4900), datei);

        t.deleteFile(datei.id());
        themeRepository.save(t);

        assertThat(inDb(datei)).isFalse();
    }

    @Test
    void geloeschtes_thema_hinterlaesst_keine_waise() {
        Datei datei = DateiBuilder.aDatei().zip().build();
        Theme t = themaMitDatei(betreuer(4900), datei);

        themeRepository.delete(t.getId());

        assertThat(themeRepository.exists(t.getId())).isFalse();
        assertThat(inDb(datei)).isFalse();
    }

    @Test
    void datei_im_betreuerprofil_hinzufuegen_und_entfernen() {
        ProfileId id = betreuer(4901);
        BetreuerProfil profil = betreuerProfilRepository.findById(id).orElseThrow();
        Datei datei = DateiBuilder.aDatei().markdown().build();

        profil.addFile(datei);
        betreuerProfilRepository.save(profil);
        assertThat(inDb(datei)).isTrue();

        profil.deleteFileById(datei.id());
        betreuerProfilRepository.save(profil);
        assertThat(inDb(datei)).isFalse();
    }
}
