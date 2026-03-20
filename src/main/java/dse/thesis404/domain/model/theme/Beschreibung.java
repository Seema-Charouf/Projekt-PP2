package dse.thesis404.domain.model.theme;

public record Beschreibung(String markdown) {
    
    public Beschreibung {
        if (markdown == null) {
            throw new IllegalArgumentException("Beschreibung darf nicht null sein!");
        }
        markdown = normalize(markdown);
    }
    
    public boolean isEmpty() {
        return markdown == null || markdown.isBlank();
    }
    
    @Override
    public String toString() {
        return markdown;
    }

    private static String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        return raw.replace("\r\n", "\n");
    }
}
