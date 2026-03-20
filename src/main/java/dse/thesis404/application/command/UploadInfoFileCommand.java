package dse.thesis404.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.InputStream;
import java.util.Objects;

public record UploadInfoFileCommand(
        @NotBlank(message = "Titel darf nicht leer sein")
        String title,
        
        String description,
        
        @NotBlank(message = "Dateiname darf nicht leer sein")
        String originalFilename,
        
        @NotNull(message = "Dateigröße muss angegeben sein")
        @Positive(message = "Dateigröße muss größer als 0 sein")
        Long sizeBytes,
        
        @NotNull(message = "Dateiinhalt darf nicht null sein")
        InputStream data
) {
    public UploadInfoFileCommand {
        Objects.requireNonNull(title, "title darf nicht null sein!");
        Objects.requireNonNull(originalFilename, "originalFilename darf nicht null sein!");
        Objects.requireNonNull(sizeBytes, "sizeBytes darf nicht null sein!");
        Objects.requireNonNull(data, "data darf nicht null sein!");
        
        if (title.isBlank()) {
            throw new IllegalArgumentException("title darf nicht leer sein!");
        }
        if (originalFilename.isBlank()) {
            throw new IllegalArgumentException("originalFilename darf nicht leer sein!");
        }
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("sizeBytes muss größer als 0 sein!");
        }
    }
}
