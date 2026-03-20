package dse.thesis404.domain.model.student;

import dse.thesis404.domain.model.shared.ProfileId;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class Student {

    private final ProfileId id;
    private String displayName;
    private final Set<String> besuchteVeranstaltungen;
    private final Set<String> interessen;

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

    public Set<String> getBesuchteVeranstaltungen() {
        return Set.copyOf(besuchteVeranstaltungen);
    }

    public Set<String> getInteressen() {
        return Set.copyOf(interessen);
    }

    public void addVeranstaltung(String name) {
        if (name != null && !name.isBlank()) {
            besuchteVeranstaltungen.add(name.trim());
        }
    }

    public void addInteresse(String name) {
        if (name != null && !name.isBlank()) {
            interessen.add(name.trim());
        }
    }

    public void setBesuchteVeranstaltungen(Set<String> names) {
        besuchteVeranstaltungen.clear();
        if (names != null) {
            names.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).forEach(besuchteVeranstaltungen::add);
        }
    }

    public void setInteressen(Set<String> names) {
        interessen.clear();
        if (names != null) {
            names.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isEmpty()).forEach(interessen::add);
        }
    }
}
