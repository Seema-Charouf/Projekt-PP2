package dse.thesis404.domain.model.theme;

import java.util.Objects;
import java.util.UUID;

public record ThemeId(UUID value) {

    public ThemeId {
        Objects.requireNonNull(value, "ThemeId value darf nicht null sein!");
    }

    public static ThemeId newId() {
        return new ThemeId(UUID.randomUUID());
    }

    public static ThemeId parse(String raw) {
        Objects.requireNonNull(raw, "raw");
        String s = raw.trim();
        try {
            return new ThemeId(UUID.fromString(s));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Ungültige UUID in ThemeId: " + raw, e);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
