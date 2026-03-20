package dse.thesis404.application.service;

import dse.thesis404.infrastructure.web.form.ThemeForm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TopicService {

    private final List<ThemeForm> themes = new ArrayList<>();

    public void addTheme(ThemeForm themeForm){
        themes.add(themeForm);
    }

    public List<ThemeForm> getAllThemes(){
        return themes;
    }

    public void deleteTheme(UUID id){
        themes.removeIf(theme -> theme.getId().equals(id));
    }

    public ThemeForm getThemeById(UUID id){
        return themes.stream()
                .filter(theme -> theme.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
