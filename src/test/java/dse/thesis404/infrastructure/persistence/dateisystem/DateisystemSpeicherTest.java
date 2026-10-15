package dse.thesis404.infrastructure.persistence.dateisystem;

import dse.thesis404.application.port.FileStoragePort.FileStorageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DateisystemSpeicher – Dateien überleben einen Neustart")
class DateisystemSpeicherTest {

    @TempDir
    Path verzeichnis;

    private static InputStream inhalt(String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }

    private static String lesen(InputStream in) throws Exception {
        try (in) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void speichern_und_laden() throws Exception {
        DateisystemSpeicher speicher = new DateisystemSpeicher(verzeichnis.toString());

        speicher.store("abc.pdf", inhalt("PDF-Inhalt"));

        assertThat(speicher.exists("abc.pdf")).isTrue();
        assertThat(lesen(speicher.load("abc.pdf").orElseThrow())).isEqualTo("PDF-Inhalt");
        assertThat(Files.exists(verzeichnis.resolve("abc.pdf"))).isTrue();
    }

    @Test
    void datei_ist_nach_neustart_noch_da() throws Exception {
        new DateisystemSpeicher(verzeichnis.toString()).store("bleibt.md", inhalt("# Hallo"));

        DateisystemSpeicher nachNeustart = new DateisystemSpeicher(verzeichnis.toString());

        assertThat(lesen(nachNeustart.load("bleibt.md").orElseThrow())).isEqualTo("# Hallo");
    }

    @Test
    void loeschen() {
        DateisystemSpeicher speicher = new DateisystemSpeicher(verzeichnis.toString());
        speicher.store("weg.zip", inhalt("x"));

        assertThat(speicher.delete("weg.zip")).isTrue();
        assertThat(speicher.exists("weg.zip")).isFalse();
        assertThat(speicher.delete("weg.zip")).isFalse();
        assertThat(speicher.load("weg.zip")).isEmpty();
    }

    @Test
    void vorhandene_datei_wird_nicht_ueberschrieben() throws Exception {
        DateisystemSpeicher speicher = new DateisystemSpeicher(verzeichnis.toString());
        speicher.store("einmal.pdf", inhalt("original"));

        assertThatThrownBy(() -> speicher.store("einmal.pdf", inhalt("fremd")))
                .isInstanceOf(FileStorageException.class);
        assertThat(lesen(speicher.load("einmal.pdf").orElseThrow())).isEqualTo("original");
    }

    @Test
    void path_traversal_wird_abgelehnt() {
        DateisystemSpeicher speicher = new DateisystemSpeicher(verzeichnis.resolve("uploads").toString());

        assertThatThrownBy(() -> speicher.store("../ausbruch.pdf", inhalt("x")))
                .isInstanceOf(FileStorageException.class);
        assertThatThrownBy(() -> speicher.load("../../etc/passwd"))
                .isInstanceOf(FileStorageException.class);
        assertThat(Files.exists(verzeichnis.resolve("ausbruch.pdf"))).isFalse();
    }
}
