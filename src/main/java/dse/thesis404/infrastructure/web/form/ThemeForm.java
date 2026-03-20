package dse.thesis404.infrastructure.web.form;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class ThemeForm {

    private UUID id = UUID.randomUUID();

    @NotBlank(message = "Titel darf nicht leer sein")
    private String titel;

    private String beschreibungMarkdown;

    private Set<String> requiredModules = new HashSet<>();
    private Set<String> links = new HashSet<>();
    private Set<String> fachgebiete = new HashSet<>();
    private List<DocumentMetaData> attachedFileMetaData = new ArrayList<>();
    private List<MultipartFile> files;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibungMarkdown() {
        return beschreibungMarkdown;
    }

    public void setBeschreibungMarkdown(String beschreibungMarkdown) {
        this.beschreibungMarkdown = beschreibungMarkdown;
    }

    public Set<String> getFachgebiete() {
        return fachgebiete;
    }

    public void setFachgebiete(Set<String> fachgebiete) {
        this.fachgebiete = fachgebiete;
    }

    public Set<String> getRequiredModules() {
        return requiredModules;
    }

    public void setRequiredModules(Set<String> requiredModules) {
        this.requiredModules = requiredModules;
    }

    public Set<String> getLinks() {
        return links;
    }

    public void setLinks(Set<String> links) {
        this.links = links;
    }

    public List<DocumentMetaData> getAttachedFileMetaData() {
        return attachedFileMetaData;
    }

    public void setAttachedFileMetaData(List<DocumentMetaData> attachedFileMetaData) {
        this.attachedFileMetaData = attachedFileMetaData;
    }

    public List<MultipartFile> getFiles() {
        return files;
    }

    public void setFiles(List<MultipartFile> files) {
        this.files = files;
    }
}
