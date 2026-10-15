package dse.thesis404.domain.model.betreuer;

import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.Fachgebiet;
import dse.thesis404.domain.model.shared.FileId;
import dse.thesis404.domain.model.shared.Link;
import dse.thesis404.domain.model.shared.ProfileId;

import java.util.*;

public final class BetreuerProfil {
    private final ProfileId id;
    private Titel titel;
    private String name;
    private ContactInformations contactInfos;
    private final Set<Fachgebiet> fachgebiete;
    private final List<Link> links;
    private final List<Datei> files;

    public BetreuerProfil(ProfileId id, String name, ContactInformations contactInfos) {
        this(id, Titel.NONE, name, contactInfos);
    }

    public BetreuerProfil(ProfileId id, Titel titel, String name, ContactInformations contactInfos) {
        this.id = Objects.requireNonNull(id, "ID darf nicht null sein!");
        this.titel = titel != null ? titel : Titel.NONE;
        this.name = normalizeName(Objects.requireNonNull(name, "Name darf nicht null sein!"));
        this.contactInfos = Objects.requireNonNull(contactInfos, "Kontaktinformationen darf nicht null sein!");
        this.fachgebiete = new LinkedHashSet<>();
        this.links = new ArrayList<>();
        this.files = new ArrayList<>();
    }
    
    private static String normalizeName(String name) {
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name darf nicht leer oder nur Leerzeichen sein!");
        }
        String trimmed = name.trim();
        return trimmed.replaceAll("\\s+", " ");
    }

    public ProfileId getId() {
        return id;
    }
    public Titel getTitel() {
        return titel;
    }
    public String getName(){
        return name;
    }
    
    public String getFullName() {
        return titel.getDisplayNameWithSpace() + name;
    }
    public Email getMail(){
        return contactInfos.email();
    }
    public PhoneNumber getPhoneNumber(){
        return contactInfos.phoneNumber();
    }
    public Raum getRaum(){
        return contactInfos.raum();
    }
    public Set<Fachgebiet> getFachgebiete(){
        return Set.copyOf(fachgebiete);
    }
    public List<Link> getLinks(){
        return List.copyOf(links);
    }
    public List<Datei> getFiles(){
        return List.copyOf(files);
    }

    public void addFachgebiet(String fachgebietName){
        Fachgebiet fachgebiet = new Fachgebiet(fachgebietName);
        fachgebiete.add(fachgebiet);
    }
    public void addLink(String urlString, String linkText){
        Link link = Link.fromTextToURL(urlString, linkText);
        links.add(link);
    }
    public void addFile(Datei file) {
        Objects.requireNonNull(file, "Datei darf nicht null sein!");
        this.files.add(file);
    }

    public void deleteFachgebiet(String fachgebietName){
        Fachgebiet fachgebiet = new Fachgebiet(fachgebietName);
        fachgebiete.remove(fachgebiet);
    }
    public void deleteFachgebietByKey(String key){
        String normalized = new Fachgebiet(key).key();
        fachgebiete.removeIf(fg -> fg.key().equals(normalized));
    }
    public void deleteLink(String urlString, String linkText){
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
    public void deleteFileById(FileId fileId){
        Objects.requireNonNull(fileId, "FileId darf nicht null sein!");
        this.files.removeIf(f -> f.id().equals(fileId));
    }

    public void updateTitel(Titel titel) {
        this.titel = titel != null ? titel : Titel.NONE;
    }
    public void updateName(String name){
        this.name = normalizeName(Objects.requireNonNull(name, "Name darf nicht null sein!"));
    }
    public void updateEmail(String email){
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email darf nicht null oder leer sein!");
        }
        Email parsedEmail = new Email(email.trim());
        this.contactInfos = this.contactInfos.withEmail(parsedEmail);
    }
    public void updatePhoneNumber(String phone) {
        if (phone != null && phone.isBlank()) {
            phone = null; 
        }
        PhoneNumber parsed = phone != null ? new PhoneNumber(phone.trim()) : null;
        this.contactInfos = this.contactInfos.withPhoneNumber(parsed);
    }
    public void updateRaum(String raum){
        if (raum == null || raum.isBlank()) {
            throw new IllegalArgumentException("Raum darf nicht null oder leer sein!");
        }
        Raum parsedRaum = Raum.of(raum.trim());
        this.contactInfos = this.contactInfos.withRaum(parsedRaum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BetreuerProfil other)) return false;
        return id.equals(other.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
