package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table("theme")
public class ThemeAggregate implements Persistable<UUID> {

    @Id
    @Column("theme_id")
    private java.util.UUID themeId;

    @Column("owner_profile_id")
    private Long ownerProfileId;

    private String titel;
    private String beschreibung;

    /** Spring Data JDBC: UPDATE ... WHERE theme_id = ? AND version = ?  – sonst OptimisticLockingFailureException */
    @Version
    private Long version;

    @MappedCollection(idColumn = "theme_id")
    private Set<ThemeFachgebiet> fachgebiete = new HashSet<>();

    @MappedCollection(idColumn = "theme_id")
    private Set<ThemeVeranstaltung> veranstaltungen = new HashSet<>();

    @MappedCollection(idColumn = "theme_id")
    private Set<ThemeLink> links = new HashSet<>();

    @MappedCollection(idColumn = "theme_id")
    private Set<ThemeDateiRef> fileRefs = new HashSet<>();

    
    @Transient
    private boolean isNew = true;

    public ThemeAggregate() {}

    @Override
    public UUID getId() {
        return themeId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public java.util.UUID getThemeId() {
        return themeId;
    }

    public void setThemeId(java.util.UUID themeId) {
        this.themeId = themeId;
    }

    public Long getOwnerProfileId() {
        return ownerProfileId;
    }

    public void setOwnerProfileId(Long ownerProfileId) {
        this.ownerProfileId = ownerProfileId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public Set<ThemeFachgebiet> getFachgebiete() {
        return fachgebiete;
    }

    public void setFachgebiete(Set<ThemeFachgebiet> fachgebiete) {
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
    }

    public Set<ThemeVeranstaltung> getVeranstaltungen() {
        return veranstaltungen;
    }

    public void setVeranstaltungen(Set<ThemeVeranstaltung> veranstaltungen) {
        this.veranstaltungen = veranstaltungen != null ? veranstaltungen : new HashSet<>();
    }

    public Set<ThemeLink> getLinks() {
        return links;
    }

    public void setLinks(Set<ThemeLink> links) {
        this.links = links != null ? links : new HashSet<>();
    }

    public Set<ThemeDateiRef> getFileRefs() {
        return fileRefs;
    }

    public void setFileRefs(Set<ThemeDateiRef> fileRefs) {
        this.fileRefs = fileRefs != null ? fileRefs : new HashSet<>();
    }
}
