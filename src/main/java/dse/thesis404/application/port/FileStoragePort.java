package dse.thesis404.application.port;

import java.io.InputStream;
import java.util.Optional;

public interface FileStoragePort {
    
    
    void store(String storedFilename, InputStream fileContent);
    
    
    Optional<InputStream> load(String storedFilename);
    
    
    boolean delete(String storedFilename);
    
    
    boolean exists(String storedFilename);
    
    
    class FileStorageException extends RuntimeException {
        public FileStorageException(String message) {
            super(message);
        }
        
        public FileStorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

