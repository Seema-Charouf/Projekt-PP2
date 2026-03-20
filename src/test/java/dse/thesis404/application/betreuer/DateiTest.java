package dse.thesis404.application.betreuer;

import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.Datei.FileType;
import dse.thesis404.testutil.DateiBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Datei – Validierung & Invarianten")
class DateiTest {

    @Nested
    @DisplayName("Dateitypen nur pdf, zip, md")
    class FileTypes {

        @Test
        void pdf_erlaubt() {
            Datei d = DateiBuilder.aDatei().pdf().build();
            assertThat(d.type()).isEqualTo(FileType.PDF);
        }

        @Test
        void zip_erlaubt() {
            Datei d = DateiBuilder.aDatei().zip().build();
            assertThat(d.type()).isEqualTo(FileType.ZIP);
        }

        @Test
        void markdown_erlaubt() {
            Datei d = DateiBuilder.aDatei().markdown().build();
            assertThat(d.type()).isEqualTo(FileType.MARKDOWN);
        }

        @Test
        void fromOriginalFilename_ungueltig_wirft() {
            assertThatThrownBy(() -> FileType.fromOriginalFilename("x.exe"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("PDF");
        }
    }

    @Nested
    @DisplayName("max 10MB")
    class SizeLimit {

        @Test
        void unter_10mb_ok() {
            Datei d = DateiBuilder.aDatei().sizeBytes(1024).build();
            assertThat(d.sizeBytes()).isEqualTo(1024);
        }

        @Test
        void ueber_10mb_wirft() {
            assertThatThrownBy(() -> DateiBuilder.aDatei()
                    .sizeBytes(11 * 1024 * 1024).build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("10 MB");
        }
    }

    @Nested
    @DisplayName("Metadaten gesetzt")
    class Metadata {

        @Test
        void uploader_und_zeitpunkt_gesetzt() {
            Datei d = DateiBuilder.aDatei()
                    .uploader("gh:1001")
                    .build();
            assertThat(d.uploaderId()).isEqualTo("gh:1001");
            assertThat(d.uploadedAt()).isNotNull();
        }
    }
}
