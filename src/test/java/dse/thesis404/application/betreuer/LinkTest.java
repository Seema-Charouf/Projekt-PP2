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

    @Nested
    @DisplayName("Nur http/https erlaubt (Schutz vor XSS über Links)")
    class Schemas {

        @Test
        void http_ist_erlaubt() {
            assertThat(Link.fromTextToURL("http://hhu.de", "HHU").url().getProtocol()).isEqualTo("http");
        }

        @Test
        void https_gross_geschrieben_ist_erlaubt() {
            assertThat(Link.fromTextToURL("HTTPS://hhu.de", "HHU").text()).isEqualTo("HHU");
        }

        @Test
        void javascript_link_wird_abgelehnt() {
            assertThatThrownBy(() -> Link.fromTextToURL("javascript:alert('XSS')", "Klick"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void file_link_wird_abgelehnt() {
            assertThatThrownBy(() -> Link.fromTextToURL("file:///etc/passwd", "Datei"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("http");
        }

        @Test
        void ftp_link_wird_abgelehnt() {
            assertThatThrownBy(() -> Link.fromTextToURL("ftp://hhu.de/datei", "FTP"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("http");
        }
    }
}
