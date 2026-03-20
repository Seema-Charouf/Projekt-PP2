package dse.thesis404.application.betreuer;

import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.betreuer.ContactInformations;
import dse.thesis404.domain.model.betreuer.Email;
import dse.thesis404.domain.model.betreuer.Raum;
import dse.thesis404.domain.model.shared.ProfileId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BetreuerProfil – Invarianten")
class BetreuerProfilTest {

    private static ContactInformations kontakt() {
        return new ContactInformations(
                new Email("a@b.de"),
                null,
                Raum.of("25.12.01.01"));
    }

    @Nested
    @DisplayName("Konstruktor")
    class Constructor {

        @Test
        void name_nicht_leer() {
            assertThatThrownBy(() -> new BetreuerProfil(
                    ProfileId.ofGithubUserId(1), "", kontakt()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name");
        }

        @Test
        void id_nicht_null() {
            assertThatThrownBy(() -> new BetreuerProfil(null, "X", kontakt()))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        void gueltiges_profil() {
            BetreuerProfil p = new BetreuerProfil(
                    ProfileId.ofGithubUserId(1), "Karl", kontakt());
            assertThat(p.getName()).isEqualTo("Karl");
            assertThat(p.getFachgebiete()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Fachgebiete unique")
    class Fachgebiete {

        @Test
        void fachgebiete_eindeutig() {
            BetreuerProfil p = new BetreuerProfil(
                    ProfileId.ofGithubUserId(1), "Karl", kontakt());
            p.addFachgebiet("Compilerbau");
            p.addFachgebiet("Compilerbau");
            p.addFachgebiet("compilerbau");
            assertThat(p.getFachgebiete()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Links brauchen URL + Text")
    class Links {

        @Test
        void link_mit_url_und_text() {
            BetreuerProfil p = new BetreuerProfil(
                    ProfileId.ofGithubUserId(1), "Karl", kontakt());
            p.addLink("https://example.com", "Example");
            assertThat(p.getLinks()).hasSize(1);
            assertThat(p.getLinks().getFirst().url().toString()).contains("example.com");
            assertThat(p.getLinks().getFirst().text()).isEqualTo("Example");
        }

        @Test
        void link_url_leer_wirft() {
            BetreuerProfil p = new BetreuerProfil(
                    ProfileId.ofGithubUserId(1), "Karl", kontakt());
            assertThatThrownBy(() -> p.addLink("", "X"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("URL");
        }
    }
}
