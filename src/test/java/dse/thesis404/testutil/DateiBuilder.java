package dse.thesis404.testutil;

import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.FileId;
import dse.thesis404.domain.model.shared.Datei.FileType;

import java.time.Instant;

public final class DateiBuilder {

    private FileId id = FileId.newId();
    private String title = "Test-Datei";
    private String description = null;
    private String uploaderId = "gh:1001";
    private Instant uploadedAt = Instant.now();
    private FileType type = FileType.MARKDOWN;
    private long sizeBytes = 1024;
    private String storedFilename = "test.md";
    private String originalFilename = "test.md";

    private DateiBuilder() {}

    public static DateiBuilder aDatei() {
        return new DateiBuilder();
    }

    public DateiBuilder pdf() {
        type = FileType.PDF;
        storedFilename = "doc.pdf";
        originalFilename = "doc.pdf";
        return this;
    }

    public DateiBuilder zip() {
        type = FileType.ZIP;
        storedFilename = "archiv.zip";
        originalFilename = "archiv.zip";
        return this;
    }

    public DateiBuilder markdown() {
        type = FileType.MARKDOWN;
        storedFilename = "readme.md";
        originalFilename = "readme.md";
        return this;
    }

    public DateiBuilder sizeBytes(long bytes) {
        this.sizeBytes = bytes;
        return this;
    }

    public DateiBuilder uploader(String uploaderId) {
        this.uploaderId = uploaderId;
        return this;
    }

    public Datei build() {
        return Datei.createForUpload(
                title, description, uploaderId, uploadedAt,
                type, sizeBytes, storedFilename, originalFilename);
    }

    public Datei buildFromPersistence() {
        return Datei.fromPersistence(
                id, title, description, uploaderId, uploadedAt,
                type, sizeBytes, storedFilename, originalFilename);
    }
}
