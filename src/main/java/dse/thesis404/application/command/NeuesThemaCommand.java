package dse.thesis404.application.command;

import java.util.Set;

/** Eingabedaten für ein neues Thema – unabhängig vom Web-Formular. */
public record NeuesThemaCommand(String titel, String beschreibung, Set<String> fachgebiete, Set<String> veranstaltungen) {
    public NeuesThemaCommand {
        fachgebiete = fachgebiete == null ? Set.of() : Set.copyOf(fachgebiete);
        veranstaltungen = veranstaltungen == null ? Set.of() : Set.copyOf(veranstaltungen);
    }
}
