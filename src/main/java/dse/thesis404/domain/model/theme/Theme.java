package dse.thesis404.domain.model.theme;

import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.Fachgebiet;
import dse.thesis404.domain.model.shared.FileId;
import dse.thesis404.domain.model.shared.Link;
import dse.thesis404.domain.model.shared.ProfileId;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Theme {
    private final ThemeId id;
    private final ProfileId ownerProfileId;
    private String titel;
    private Beschreibung beschreibung;
    private final Set<Veranstaltung> angeforderteVeranstaltungen;
    private final List<Datei> files;
    private final List<Link> links;
    private final Set<Fachgebiet> fachgebiete;
    /** Technische Versionsnummer für Optimistic Locking; null = noch nie gespeichert. */
    private Long version;

    public Theme(ThemeId id, ProfileId ownerProfileId, String titel, Beschreibung beschreibung) {
        this.id = Objects.requireNonNull(id, "ThemeId darf nicht null sein!");
        this.ownerProfileId = Objects.requireNonNull(ownerProfileId, "Owner ProfileId darf nicht null sein!");
        this.titel = normalizeTitel(Objects.requireNonNull(titel, "Titel darf nicht null sein!"));
        this.beschreibung = Objects.requireNonNull(beschreibung, "Beschreibung darf nicht null sein!");
        this.angeforderteVeranstaltungen = new LinkedHashSet<>();
        this.files = new ArrayList<>();
        this.links = new ArrayList<>();
        this.fachgebiete = new LinkedHashSet<>();
    }
    
    private static String normalizeTitel(String titel) {
        if (titel.isBlank()) {
            throw new IllegalArgumentException("Titel darf nicht leer oder nur Leerzeichen sein!");
        }
        String trimmed = titel.trim();
        return trimmed.replaceAll("\\s+", " ");
    }
    

    public Long getVersion() {
        return version;
    }

    /** Wird nur von der Persistenz aufgerufen (nach Laden bzw. Speichern). */
    public void versionUebernehmen(Long version) {
        this.version = version;
    }

    public ThemeId getId() {
        return id;
    }
    public ProfileId getOwnerProfileId() {
        return ownerProfileId;
    }
    public String getTitel() {
        return titel;
    }
    public Beschreibung getBeschreibung() {
        return beschreibung;
    }
    public Set<Veranstaltung> getAngeforderteVeranstaltungen() {
        return Set.copyOf(angeforderteVeranstaltungen);
    }
    public List<Datei> getFiles() {
        return List.copyOf(files);
    }
    public List<Link> getLinks() {
        return List.copyOf(links);
    }
    public Set<Fachgebiet> getFachgebiete() {
        return Set.copyOf(fachgebiete);
    }

    public void addVeranstaltung(String veranstaltungName) {
        Veranstaltung veranstaltung = new Veranstaltung(veranstaltungName);
        angeforderteVeranstaltungen.add(veranstaltung);
    }
    public void addFachgebiet(String fachgebietName) {
        Fachgebiet fachgebiet = new Fachgebiet(fachgebietName);
        fachgebiete.add(fachgebiet);
    }
    public void addLink(String urlString, String linkText) {
        Link link = Link.fromTextToURL(urlString, linkText);
        links.add(link);
    }
    public void addFile(Datei file) {
        Objects.requireNonNull(file, "Datei darf nicht null sein!");
        this.files.add(file);
    }

    public void deleteVeranstaltung(String veranstaltungName) {
        Veranstaltung veranstaltung = new Veranstaltung(veranstaltungName);
        angeforderteVeranstaltungen.remove(veranstaltung);
    }
    public void deleteFachgebiet(String fachgebietName) {
        Fachgebiet fachgebiet = new Fachgebiet(fachgebietName);
        fachgebiete.remove(fachgebiet);
    }
    public void deleteLink(String urlString, String linkText) {
        if (urlString == null || urlString.isBlank()) {
            throw new IllegalArgumentException("URL darf nicht null oder leer sein!");
        }
        if (linkText == null || linkText.isBlank()) {
            throw new IllegalArgumentException("Link-Text darf nicht null oder leer sein!");
        }
        Link linkToDelete = Link.fromTextToURL(urlString.trim(), linkText.trim());
        links.removeIf(link ->
                link.url().equals(linkToDelete.url()) &&
                link.text().equals(linkToDelete.text())
        );
    }

    public void deleteFile(FileId fileId){
        Objects.requireNonNull(fileId, "FileId darf nicht null sein!");
        this.files.removeIf(f -> f.id().equals(fileId));
    }

    public void updateTitel(String titel) {
        this.titel = normalizeTitel(Objects.requireNonNull(titel, "Titel darf nicht null sein!"));
    }
    public void updateBeschreibung(String markdown) {
        if (markdown == null) {
            throw new IllegalArgumentException("Beschreibung darf nicht null sein!");
        }
        this.beschreibung = new Beschreibung(markdown);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Theme other)) return false;
        return id.equals(other.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
