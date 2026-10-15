package dse.thesis404.infrastructure.persistence.inmemory;

import dse.thesis404.application.port.FileStoragePort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Nur für Tests: schnell, aber nach einem Neustart sind alle Dateien weg. */
@Component
@Profile("test")
public class InMemoryFileStorage implements FileStoragePort {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public void store(String storedFilename, InputStream fileContent) {
        try {
            byte[] bytes = fileContent.readAllBytes();
            storage.put(storedFilename, bytes);
        } catch (IOException e) {
            throw new FileStorageException("Fehler beim Speichern der Datei: " + storedFilename, e);
        }
    }

    @Override
    public Optional<InputStream> load(String storedFilename) {
        byte[] bytes = storage.get(storedFilename);
        if (bytes == null) {
            return Optional.empty();
        }
        return Optional.of(new ByteArrayInputStream(bytes));
    }

    @Override
    public boolean delete(String storedFilename) {
        return storage.remove(storedFilename) != null;
    }

    @Override
    public boolean exists(String storedFilename) {
        return storage.containsKey(storedFilename);
    }
}
