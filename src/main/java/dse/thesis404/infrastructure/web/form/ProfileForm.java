package dse.thesis404.infrastructure.web.form;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileForm {

    @NotBlank (message = "name darf nicht leeer sein")
    private String name;
    private Set<String> fachgebiete = new HashSet<>();
    private List <MultipartFile> files;

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public Set<String> getFachgebiete() {
        return fachgebiete;
    }
    public void setFachgebiete(Set<String> fachgebiete) {
        this.fachgebiete = fachgebiete;
    }

    public List <MultipartFile> getFiles(){
        return files;
    }
    public void setFiles(List <MultipartFile> files){
        this.files = files;
    }

}
