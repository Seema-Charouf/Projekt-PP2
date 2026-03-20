package dse.thesis404.domain.model.theme;

import java.util.Locale;
import java.util.Objects;

public record Veranstaltung(String name) {

    public Veranstaltung {
        name = sanitize(name);
    }

    public String key() {
        return normalizeKey(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Veranstaltung other)) return false;
        return this.key().equals(other.key());
    }

    @Override
    public int hashCode() {
        return Objects.hash(key());
    }

    @Override
    public String toString() {
        return name;
    }

    private static String sanitize(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Veranstaltung darf nicht null sein!");
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Veranstaltung darf nicht leer sein!");
        }

        return trimmed.replaceAll("\\s+", " ");
    }
    private static String normalizeKey(String cleanedName) {
        return cleanedName.toLowerCase(Locale.ROOT);
    }
}
