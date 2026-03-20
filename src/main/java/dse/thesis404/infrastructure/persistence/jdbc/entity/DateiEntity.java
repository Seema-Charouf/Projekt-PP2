package dse.thesis404.infrastructure.persistence.jdbc.entity;

import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.FileId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("datei")
public class DateiEntity {

    @Id
    @Column("file_id")
    private UUID fileId;

    private String title;
    private String beschreibung;

    @Column("uploader_id")
    private String uploaderId;

    @Column("uploaded_at")
    private Instant uploadedAt;

    private String type;

    @Column("size_bytes")
    private long sizeBytes;

    @Column("stored_filename")
    private String storedFilename;

    @Column("original_filename")
    private String originalFilename;

    public DateiEntity() {}

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public void setStoredFilename(String storedFilename) {
        this.storedFilename = storedFilename;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public static DateiEntity fromDomain(Datei domain) {
        var e = new DateiEntity();
        e.setFileId(domain.id().value());
        e.setTitle(domain.title());
        e.setBeschreibung(domain.description());
        e.setUploaderId(domain.uploaderId());
        e.setUploadedAt(domain.uploadedAt());
        e.setType(domain.type().name());
        e.setSizeBytes(domain.sizeBytes());
        e.setStoredFilename(domain.storedFilename());
        e.setOriginalFilename(domain.originalFilename());
        return e;
    }

    public Datei toDomain() {
        return Datei.fromPersistence(
                new FileId(fileId),
                title,
                beschreibung,
                uploaderId,
                uploadedAt,
                Datei.FileType.valueOf(type),
                sizeBytes,
                storedFilename,
                originalFilename
        );
    }
}
