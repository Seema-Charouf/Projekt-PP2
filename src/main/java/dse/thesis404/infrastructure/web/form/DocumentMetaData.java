package dse.thesis404.infrastructure.web.form;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentMetaData {
    private UUID id;
    private String title;
    private String description;
    private String storedFilename;
    private String originalFilename;
    private String uploaderName;
    private LocalDateTime uploadDate;
    private String fileType;

    public DocumentMetaData(String title, String description, String originalFilename, String storedFilename, String uploaderName, String fileType) {
        this.id = UUID.randomUUID();
        this.uploadDate = LocalDateTime.now();

        this.title = title;
        this.description = description;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.uploaderName = uploaderName;
        this.fileType = fileType;
    }

    public UUID getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public String getStoredFilename(){
        return storedFilename;
    }
    public String getOriginalFilename(){
        return originalFilename;
    }
    public String getUploaderName(){
        return uploaderName;
    }
    public LocalDateTime getUploadDate(){
        return uploadDate;
    }
    public String getFileType(){
        return fileType;
    }

}
