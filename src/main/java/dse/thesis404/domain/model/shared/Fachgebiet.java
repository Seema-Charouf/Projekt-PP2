package dse.thesis404.domain.model.shared;

import java.util.Locale;
import java.util.Objects;

public record Fachgebiet(String fachgebiet) {

    public Fachgebiet {
        fachgebiet = sanitize(fachgebiet);
    }

    public String key() {
        return normalizeKey(fachgebiet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Fachgebiet other)) return false;
        return this.key().equals(other.key());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key());
    }

    @Override
    public String toString() {
        return fachgebiet;
    }

    private static String sanitize(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Fachgebiet darf nicht null sein!");
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Fachgebiet darf nicht leer sein!");
        }

        return trimmed.replaceAll("\\s+", " ");
    }
    private static String normalizeKey(String cleanedName) {
        return cleanedName.toLowerCase(Locale.ROOT);
    }
}
