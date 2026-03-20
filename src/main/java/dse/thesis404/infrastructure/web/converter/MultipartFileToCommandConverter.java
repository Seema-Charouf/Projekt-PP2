package dse.thesis404.infrastructure.web.converter;

import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.infrastructure.web.form.DocumentForm;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Component
public class MultipartFileToCommandConverter {
    
    
    public UploadInfoFileCommand convert(
            MultipartFile file,
            String title,
            String description
    ) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Datei darf nicht leer sein!");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("Dateiname darf nicht leer sein!");
        }
        
        long sizeBytes = file.getSize();
        if (sizeBytes <= 0) {
            throw new IllegalArgumentException("Dateigröße muss größer als 0 sein!");
        }
        
        InputStream inputStream = file.getInputStream();
        
        return new UploadInfoFileCommand(
                title,
                description,
                originalFilename,
                sizeBytes,
                inputStream
        );
    }
    
    
    public UploadInfoFileCommand convertWithFilenameAsTitle(
            MultipartFile file,
            String description
    ) throws IOException {
        String originalFilename = file != null ? file.getOriginalFilename() : null;
        String title = originalFilename != null && !originalFilename.isBlank() 
                ? originalFilename 
                : "Unbenannt";
        
        return convert(file, title, description);
    }
    
    
    public UploadInfoFileCommand convertFromDocumentForm(DocumentForm documentForm) throws IOException {
        if (documentForm == null) {
            throw new IllegalArgumentException("DocumentForm darf nicht null sein!");
        }
        
        MultipartFile file = documentForm.getFile();
        String title = documentForm.getTitle();
        String description = documentForm.getDescription();
        
        return convert(file, title, description);
    }
}
