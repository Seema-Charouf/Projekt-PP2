package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Table("student")
public class StudentAggregate implements Persistable<Long> {

    @Id
    @Column("profile_id")
    private Long profileId;

    @Column("display_name")
    private String displayName;

    @Column("created_at")
    private Instant createdAt;

    @MappedCollection(idColumn = "profile_id")
    private Set<StudentVeranstaltung> veranstaltungen = new HashSet<>();

    @MappedCollection(idColumn = "profile_id")
    private Set<StudentFachgebiet> fachgebiete = new HashSet<>();

    @Transient
    private boolean isNew = true;

    public StudentAggregate() {}

    @Override
    public Long getId() {
        return profileId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Set<StudentVeranstaltung> getVeranstaltungen() {
        return veranstaltungen;
    }

    public void setVeranstaltungen(Set<StudentVeranstaltung> veranstaltungen) {
        this.veranstaltungen = veranstaltungen != null ? veranstaltungen : new HashSet<>();
    }

    public Set<StudentFachgebiet> getFachgebiete() {
        return fachgebiete;
    }

    public void setFachgebiete(Set<StudentFachgebiet> fachgebiete) {
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
    }
}
