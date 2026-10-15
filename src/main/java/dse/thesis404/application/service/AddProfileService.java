package dse.thesis404.application.service;

import dse.thesis404.application.command.NeuesProfilCommand;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

// TODO: Platzhalter aus dem Uni-Projekt – speichert nur im Speicher und wird nirgends gelesen.
//       Sollte später ein echtes BetreuerProfil über BetreuerProfilService anlegen.
@Service
public class AddProfileService {

    private final List<NeuesProfilCommand> profiles = new ArrayList<>();

    public void addProfile(NeuesProfilCommand command) {
        profiles.add(command);
    }

    public List<NeuesProfilCommand> getAllProfiles() {
        return List.copyOf(profiles);
    }
}
