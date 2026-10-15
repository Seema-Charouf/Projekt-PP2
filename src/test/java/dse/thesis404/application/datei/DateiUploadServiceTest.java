package dse.thesis404.application.datei;

import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.application.port.FileStoragePort;
import dse.thesis404.application.service.DateiUploadService;
import dse.thesis404.domain.model.shared.Datei;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Hier bewusst ein "Fake" statt eines Mocks: eine kleine, echte Implementierung des Ports im Speicher.
 * So kann man prüfen, WAS gespeichert wurde – nicht nur, DASS etwas aufgerufen wurde.
 */
@DisplayName("DateiUploadService – Validierung und Speichern")
class DateiUploadServiceTest {

    private static final Instant JETZT = Instant.parse("2026-10-16T08:00:00Z");

    static class SpeicherFake implements FileStoragePort {
        final Map<String, byte[]> dateien = new HashMap<>();

        @Override
        public void store(String name, InputStream in) {
            try {
                dateien.put(name, in.readAllBytes());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public Optional<InputStream> load(String name) {
            return Optional.ofNullable(dateien.get(name)).map(ByteArrayInputStream::new);
        }

        @Override
        public boolean delete(String name) {
            return dateien.remove(name) != null;
        }

        @Override
        public boolean exists(String name) {
            return dateien.containsKey(name);
        }
    }

    private SpeicherFake speicher;
    private DateiUploadService service;

    @BeforeEach
    void setUp() {
        speicher = new SpeicherFake();
        // Feste Uhr: Zeitstempel im Test vorhersagbar (deshalb wird Clock injiziert statt Instant.now())
        service = new DateiUploadService(speicher, Clock.fixed(JETZT, ZoneOffset.UTC));
    }

    private static UploadInfoFileCommand cmd(String dateiname, long angegebeneGroesse, byte[] inhalt) {
        return new UploadInfoFileCommand("Titel", "Beschreibung", dateiname, angegebeneGroesse,
                new ByteArrayInputStream(inhalt));
    }

    @Test
    void pdf_wird_unter_neuem_zufallsnamen_gespeichert() {
        byte[] inhalt = "%PDF-1.7".getBytes(StandardCharsets.UTF_8);

        Datei meta = service.storeAndCreateMeta("gh:1", cmd("bericht.pdf", inhalt.length, inhalt)).meta();

        assertThat(meta.type()).isEqualTo(Datei.FileType.PDF);
        assertThat(meta.uploadedAt()).isEqualTo(JETZT);
        assertThat(meta.originalFilename()).isEqualTo("bericht.pdf");
        assertThat(meta.storedFilename()).endsWith(".pdf").isNotEqualTo("bericht.pdf");   // UUID statt Nutzername
        assertThat(speicher.dateien.get(meta.storedFilename())).isEqualTo(inhalt);
    }

    @Test
    void markdown_wird_unveraendert_gespeichert() {
        byte[] inhalt = "# Titel\nMit <script> im Text".getBytes(StandardCharsets.UTF_8);

        Datei meta = service.storeAndCreateMeta("gh:1", cmd("notiz.md", inhalt.length, inhalt)).meta();

        assertThat(meta.type()).isEqualTo(Datei.FileType.MARKDOWN);
        assertThat(speicher.dateien.get(meta.storedFilename())).isEqualTo(inhalt);   // Schutz passiert beim Anzeigen
    }

    @Test
    void zu_grosse_datei_wird_vor_dem_speichern_abgelehnt() {
        assertThatThrownBy(() -> service.storeAndCreateMeta("gh:1", cmd("riesig.pdf", Datei.MAX_BYTES + 1, new byte[]{1})))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("zu groß");
        assertThat(speicher.dateien).isEmpty();
    }

    @Test
    void markdown_das_groesser_ist_als_angegeben_wird_abgelehnt() {
        // Die angegebene Größe stimmt nicht – der Service zählt beim Lesen selbst mit
        byte[] zuViel = new byte[(int) Datei.MAX_BYTES + 1];

        assertThatThrownBy(() -> service.storeAndCreateMeta("gh:1", cmd("luege.md", 10, zuViel)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("zu groß");
        assertThat(speicher.dateien).isEmpty();
    }

    @Test
    void leere_markdown_datei_wird_abgelehnt() {
        assertThatThrownBy(() -> service.storeAndCreateMeta("gh:1", cmd("leer.md", 1, new byte[0])))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leer");
        assertThat(speicher.dateien).isEmpty();
    }

    @Test
    void nicht_erlaubter_dateityp_wird_abgelehnt() {
        assertThatThrownBy(() -> service.storeAndCreateMeta("gh:1", cmd("virus.exe", 3, new byte[]{1, 2, 3})))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PDF, ZIP oder MD");
        assertThat(speicher.dateien).isEmpty();
    }

    @Test
    void loeschen_entfernt_die_datei_aus_dem_speicher() {
        byte[] inhalt = {1, 2, 3};
        Datei meta = service.storeAndCreateMeta("gh:1", cmd("a.zip", 3, inhalt)).meta();

        assertThat(service.deleteFromStorage(meta)).isTrue();
        assertThat(speicher.exists(meta.storedFilename())).isFalse();
    }
}
