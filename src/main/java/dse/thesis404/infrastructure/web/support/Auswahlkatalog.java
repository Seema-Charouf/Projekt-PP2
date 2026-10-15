package dse.thesis404.infrastructure.web.support;

import dse.thesis404.domain.model.theme.Veranstaltung;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Auswahllisten für die Betreuer-Formulare. Vorher fest im Controller verdrahtet;
 * hier an einer Stelle, damit sie später z. B. aus der Datenbank oder Konfiguration kommen können.
 */
@Component
public class Auswahlkatalog {

    private static final List<String> FACHGEBIETE = List.of(
            "Software Engineering",
            "Künstliche Intelligenz",
            "Datenbanken",
            "IT-Sicherheit",
            "Verteilte Systeme"
    );

    private static final List<Veranstaltung> VERANSTALTUNGEN = List.of(
            new Veranstaltung("Programmierung"),
            new Veranstaltung("Algorithmen und Datenstrukturen"),
            new Veranstaltung("Theoretische Informatik"),
            new Veranstaltung("Softwaretechnik"),
            new Veranstaltung("Datenbanksysteme"),
            new Veranstaltung("Rechnernetze"),
            new Veranstaltung("Betriebssysteme"),
            new Veranstaltung("Propra 1"),
            new Veranstaltung("Propra 2"),
            new Veranstaltung("Lineare Algebra"),
            new Veranstaltung("Analysis")
    );

    public List<String> fachgebiete() {
        return FACHGEBIETE;
    }

    public List<Veranstaltung> veranstaltungen() {
        return VERANSTALTUNGEN;
    }
}
