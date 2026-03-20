package dse.thesis404.domain.model.shared;

import java.util.Objects;
import java.util.UUID;

public record FileId(UUID value) {
    
    public FileId {
        Objects.requireNonNull(value, "value");
    }
    
    public static FileId newId() {
        return new FileId(UUID.randomUUID());
    }
    
    public static FileId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        String s = raw.trim();
        try {
            return new FileId(UUID.fromString(s));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ungültige UUID in FileId: " + raw, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
