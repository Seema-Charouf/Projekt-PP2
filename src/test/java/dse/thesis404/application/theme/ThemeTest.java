package dse.thesis404.application.theme;

import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Theme – Invarianten")
class ThemeTest {

    private static Theme create(String titel) {
        return new Theme(
                ThemeId.newId(),
                ProfileId.ofGithubUserId(1),
                titel,
                new Beschreibung("B"));
    }

    @Nested
    @DisplayName("Titel nicht leer")
    class Titel {

        @Test
        void titel_leer_wirft() {
            assertThatThrownBy(() -> create(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Titel");
        }

        @Test
        void titel_gueltig() {
            Theme t = create("Compilerbau");
            assertThat(t.getTitel()).isEqualTo("Compilerbau");
        }
    }

    @Nested
    @DisplayName("Fachgebiete unique")
    class Fachgebiete {

        @Test
        void fachgebiete_eindeutig() {
            Theme t = create("A");
            t.addFachgebiet("SE");
            t.addFachgebiet("SE");
            t.addFachgebiet("se");
            assertThat(t.getFachgebiete()).hasSize(1);
        }
    }
}
