package dse.thesis404.infrastructure.persistence.dateisystem;

import dse.thesis404.application.port.FileStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Speichert hochgeladene Dateien auf der Festplatte statt im Arbeitsspeicher.
 * Zweiter Adapter für denselben Port: Die Application-Schicht merkt vom Wechsel nichts (Onion-Prinzip).
 * Gilt in allen Profilen außer "test" – dort bleibt der schnelle InMemoryFileStorage.
 */
@Component
@Profile("!test")
public class DateisystemSpeicher implements FileStoragePort {

    private final Path wurzel;

    public DateisystemSpeicher(@Value("${app.storage.verzeichnis:./data/uploads}") String verzeichnis) {
        this.wurzel = Path.of(verzeichnis).toAbsolutePath().normalize();
        try {
            Files.createDirectories(wurzel);
        } catch (IOException e) {
            throw new FileStorageException("Upload-Verzeichnis kann nicht angelegt werden: " + wurzel, e);
        }
    }

    /** Schutz vor Path Traversal: Der Dateiname darf nie aus dem Upload-Verzeichnis herausführen. */
    private Path pfad(String storedFilename) {
        if (storedFilename == null || storedFilename.isBlank()) {
            throw new FileStorageException("Dateiname fehlt.");
        }
        Path p = wurzel.resolve(storedFilename).normalize();
        if (!wurzel.equals(p.getParent())) {
            throw new FileStorageException("Ungültiger Dateiname: " + storedFilename);
        }
        return p;
    }

    @Override
    public void store(String storedFilename, InputStream fileContent) {
        Path ziel = pfad(storedFilename);
        try (InputStream in = fileContent) {
            Files.copy(in, ziel);   // ohne REPLACE_EXISTING: vorhandene Dateien werden nie überschrieben
        } catch (FileAlreadyExistsException e) {
            throw new FileStorageException("Datei existiert bereits: " + storedFilename, e);
        } catch (IOException e) {
            throw new FileStorageException("Fehler beim Speichern der Datei: " + storedFilename, e);
        }
    }

    @Override
    public Optional<InputStream> load(String storedFilename) {
        Path p = pfad(storedFilename);
        if (!Files.isRegularFile(p)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.newInputStream(p));
        } catch (IOException e) {
            throw new FileStorageException("Fehler beim Lesen der Datei: " + storedFilename, e);
        }
    }

    @Override
    public boolean delete(String storedFilename) {
        try {
            return Files.deleteIfExists(pfad(storedFilename));
        } catch (IOException e) {
            throw new FileStorageException("Fehler beim Löschen der Datei: " + storedFilename, e);
        }
    }

    @Override
    public boolean exists(String storedFilename) {
        return Files.isRegularFile(pfad(storedFilename));
    }
}
