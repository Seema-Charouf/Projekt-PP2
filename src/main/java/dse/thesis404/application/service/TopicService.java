package dse.thesis404.application.service;

import dse.thesis404.application.command.NeuesThemaCommand;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO: Platzhalter aus dem Uni-Projekt – speichert nur im Speicher und wird nirgends gelesen.
//       Sollte später ein echtes Theme über ThemeService anlegen.
@Service
public class TopicService {

    private final List<NeuesThemaCommand> themes = new ArrayList<>();

    public void addTheme(NeuesThemaCommand command) {
        themes.add(command);
    }

    public List<NeuesThemaCommand> getAllThemes() {
        return List.copyOf(themes);
    }
}
