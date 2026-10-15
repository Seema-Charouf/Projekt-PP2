package dse.thesis404.application.command;

import java.util.Set;

/** Eingabedaten für ein neues Betreuerprofil – unabhängig vom Web-Formular. */
public record NeuesProfilCommand(String name, Set<String> fachgebiete) {
    public NeuesProfilCommand {
        fachgebiete = fachgebiete == null ? Set.of() : Set.copyOf(fachgebiete);
    }
}
