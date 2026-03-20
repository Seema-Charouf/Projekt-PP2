package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table("betreuer_profil")
public class BetreuerProfilAggregate implements Persistable<Long> {

    @Id
    @org.springframework.data.relational.core.mapping.Column("profile_id")
    private Long profileId;

    @org.springframework.data.relational.core.mapping.Column("github_id")
    private Long githubId;

    private String titel;
    private String name;
    private String email;
    private String phone;
    private String raum;

    @MappedCollection(idColumn = "profile_id")
    private Set<BetreuerFachgebiet> fachgebiete = new HashSet<>();

    @MappedCollection(idColumn = "profile_id")
    private Set<BetreuerLink> links = new HashSet<>();

    @MappedCollection(idColumn = "profile_id")
    private Set<BetreuerProfilDateiRef> fileRefs = new HashSet<>();

    
    @Transient
    private boolean isNew = true;

    public BetreuerProfilAggregate() {}

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

    public Long getGithubId() {
        return githubId;
    }

    public void setGithubId(Long githubId) {
        this.githubId = githubId;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRaum() {
        return raum;
    }

    public void setRaum(String raum) {
        this.raum = raum;
    }

    public Set<BetreuerFachgebiet> getFachgebiete() {
        return fachgebiete;
    }

    public void setFachgebiete(Set<BetreuerFachgebiet> fachgebiete) {
        this.fachgebiete = fachgebiete != null ? fachgebiete : new HashSet<>();
    }

    public Set<BetreuerLink> getLinks() {
        return links;
    }

    public void setLinks(Set<BetreuerLink> links) {
        this.links = links != null ? links : new HashSet<>();
    }

    public Set<BetreuerProfilDateiRef> getFileRefs() {
        return fileRefs;
    }

    public void setFileRefs(Set<BetreuerProfilDateiRef> fileRefs) {
        this.fileRefs = fileRefs != null ? fileRefs : new HashSet<>();
    }
}
