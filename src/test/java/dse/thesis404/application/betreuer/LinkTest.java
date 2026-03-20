package dse.thesis404.application.betreuer;

import dse.thesis404.domain.model.shared.Link;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Link – Validierung")
class LinkTest {

    @Nested
    @DisplayName("URL + Text erforderlich")
    class Validation {

        @Test
        void gueltiger_link() {
            Link l = Link.fromTextToURL("https://example.com", "Example");
            assertThat(l.text()).isEqualTo("Example");
            assertThat(l.url().toString()).contains("example.com");
        }

        @Test
        void url_leer_wirft() {
            assertThatThrownBy(() -> Link.fromTextToURL("", "Text"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("URL");
        }

        @Test
        void text_leer_wirft() {
            assertThatThrownBy(() -> Link.fromTextToURL("https://x.com", "   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Link-Text");
        }

        @Test
        void ungueltige_url_wirft() {
            assertThatThrownBy(() -> Link.fromTextToURL("://invalid", "X"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("URL");
        }
    }
}
