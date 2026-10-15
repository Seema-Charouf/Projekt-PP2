package dse.thesis404.domain.model.student;

import dse.thesis404.domain.model.shared.Fachgebiet;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Veranstaltung;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Student {

    private final ProfileId id;
    private String displayName;
    // Value Objects statt Strings: "Programmierung" und " programmierung " sind dieselbe Veranstaltung.
    private final Set<Veranstaltung> besuchteVeranstaltungen;
    private final Set<Fachgebiet> interessen;

    public Student(ProfileId id) {
        this.id = Objects.requireNonNull(id, "ProfileId darf nicht null sein!");
        this.displayName = null;
        this.besuchteVeranstaltungen = new LinkedHashSet<>();
        this.interessen = new LinkedHashSet<>();
    }

    public ProfileId getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName != null && !displayName.isBlank() ? displayName.trim() : null;
    }

    public Set<Veranstaltung> getBesuchteVeranstaltungen() {
        return Set.copyOf(besuchteVeranstaltungen);
    }

    public Set<Fachgebiet> getInteressen() {
        return Set.copyOf(interessen);
    }

    /** Leere Eingaben (z. B. ein nicht ausgefülltes Formularfeld) werden ignoriert. */
    public void addVeranstaltung(String name) {
        if (name != null && !name.isBlank()) {
            besuchteVeranstaltungen.add(new Veranstaltung(name));
        }
    }

    /** Leere Eingaben (z. B. ein nicht ausgefülltes Formularfeld) werden ignoriert. */
    public void addInteresse(String name) {
        if (name != null && !name.isBlank()) {
            interessen.add(new Fachgebiet(name));
        }
    }

    public void setBesuchteVeranstaltungen(Set<String> names) {
        besuchteVeranstaltungen.clear();
        if (names != null) {
            names.forEach(this::addVeranstaltung);
        }
    }

    public void setInteressen(Set<String> names) {
        interessen.clear();
        if (names != null) {
            names.forEach(this::addInteresse);
        }
    }
}
