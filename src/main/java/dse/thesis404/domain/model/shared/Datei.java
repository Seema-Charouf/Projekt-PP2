package dse.thesis404.domain.model.shared;

import java.time.Instant;
import java.util.Locale;
import java.util.Objects;

public final class Datei {

    public static final long MAX_BYTES = 10L * 1024L * 1024L;

    private final FileId id;
    private final String title;
    private final String beschreibung;
    private final String uploaderId;
    private final Instant uploadedAt;
    private final FileType type;
    private final long sizeBytes;
    private final String storedFilename;
    private final String originalFilename;

    private Datei(
            FileId id,
            String title,
            String beschreibung,
            String uploaderId,
            Instant uploadedAt,
            FileType type,
            long sizeBytes,
            String storedFilename,
            String originalFilename
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = requireNonBlank(title, "Titel darf nicht leer sein!");
        this.beschreibung = normalizeOptional(beschreibung);
        this.uploaderId = requireNonBlank(uploaderId, "Uploader darf nicht leer sein!");
        this.uploadedAt = Objects.requireNonNull(uploadedAt, "Upload-Time darf nicht null sein!");
        this.type = Objects.requireNonNull(type, "FileType darf nicht null sein!");

        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("Dateigröße muss > 0 sein!");
        }
        if (sizeBytes > MAX_BYTES) {
            throw new IllegalArgumentException("Datei ist zu groß (max 10 MB)!");
        }
        this.sizeBytes = sizeBytes;

        this.storedFilename = validateStoredFilename(storedFilename);
        this.originalFilename = normalizeOptional(originalFilename);

        if (!this.storedFilename.toLowerCase(Locale.ROOT).endsWith("." + type.extension())) {
            throw new IllegalArgumentException("Stored filename extension passt nicht zum Dateityp!");
        }
    }

    public static Datei createForUpload(
            String title,
            String description,
            String uploaderId,
            Instant uploadedAt,
            FileType type,
            long sizeBytes,
            String storedFilename,
            String originalFilename
    ) {
        return new Datei(
                FileId.newId(),
                title,
                description,
                uploaderId,
                uploadedAt,
                type,
                sizeBytes,
                storedFilename,
                originalFilename
        );
    }

    
    public static Datei fromPersistence(
            FileId id,
            String title,
            String description,
            String uploaderId,
            Instant uploadedAt,
            FileType type,
            long sizeBytes,
            String storedFilename,
            String originalFilename
    ) {
        return new Datei(
                id,
                title,
                description,
                uploaderId,
                uploadedAt,
                type,
                sizeBytes,
                storedFilename,
                originalFilename
        );
    }

    public FileId id() { return id; }
    public String title() { return title; }
    public String description() { return beschreibung; }
    public String uploaderId() { return uploaderId; }
    public Instant uploadedAt() { return uploadedAt; }
    public FileType type() { return type; }
    public long sizeBytes() { return sizeBytes; }
    public String storedFilename() { return storedFilename; }
    public String originalFilename() { return originalFilename; }

    public boolean isDownloadable() {
        return type == FileType.PDF || type == FileType.ZIP;
    }

    public boolean isInlineViewable() {
        return type == FileType.MARKDOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Datei other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public enum FileType {
        PDF("pdf"),
        ZIP("zip"),
        MARKDOWN("md");

        private final String extension;

        FileType(String extension) {
            this.extension = extension;
        }

        public String extension() {
            return extension;
        }

        public static FileType fromOriginalFilename(String originalFilename) {
            String n = requireNonBlank(originalFilename, "originalFilename");
            String lower = n.trim().toLowerCase(Locale.ROOT);

            if (lower.endsWith(".pdf")) return PDF;
            if (lower.endsWith(".zip")) return ZIP;
            if (lower.endsWith(".md"))  return MARKDOWN;

            throw new IllegalArgumentException("Nur PDF, ZIP oder MD Dateien sind erlaubt!");
        }
    }

    private static String requireNonBlank(String s, String msg) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(msg);
        return s.trim();
    }

    private static String normalizeOptional(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String validateStoredFilename(String storedFilename) {
        String s = requireNonBlank(storedFilename, "storedFilename darf nicht leer sein!");
        if (s.contains("/") || s.contains("\\") || s.contains("..")) {
            throw new IllegalArgumentException("Ungültiger stored filename!");
        }
        return s;
    }
}
