package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table("betreuer_profil_datei")
public class BetreuerProfilDateiRef {

    @Column("file_id")
    private UUID fileId;

    public BetreuerProfilDateiRef() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BetreuerProfilDateiRef that = (BetreuerProfilDateiRef) o;
        return Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId);
    }

    public BetreuerProfilDateiRef(UUID fileId) {
        this.fileId = fileId;
    }

    public UUID getFileId() {
        return fileId;
    }

    public void setFileId(UUID fileId) {
        this.fileId = fileId;
    }
}
