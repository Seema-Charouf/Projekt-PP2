package dse.thesis404.domain;

import dse.thesis404.domain.model.shared.Fachgebiet;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.Student;
import dse.thesis404.domain.model.theme.Veranstaltung;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Student – Veranstaltungen und Interessen als Value Objects")
class StudentTest {

    private final Student student = new Student(ProfileId.ofGithubUserId(1));

    @Test
    void gleiche_veranstaltung_mit_anderer_schreibweise_wird_nur_einmal_gespeichert() {
        student.addVeranstaltung("Programmierung");
        student.addVeranstaltung("  programmierung ");
        student.addVeranstaltung("PROGRAMMIERUNG");

        assertThat(student.getBesuchteVeranstaltungen())
                .containsExactly(new Veranstaltung("Programmierung"));
    }

    @Test
    void gleiches_interesse_mit_doppelten_leerzeichen_wird_nur_einmal_gespeichert() {
        student.addInteresse("Software Engineering");
        student.addInteresse("software   engineering");

        assertThat(student.getInteressen()).hasSize(1);
        assertThat(student.getInteressen()).contains(new Fachgebiet("Software Engineering"));
    }

    @Test
    void leere_eingaben_werden_ignoriert() {
        Set<String> eingaben = new LinkedHashSet<>();
        eingaben.add("");
        eingaben.add("   ");
        eingaben.add(null);
        eingaben.add("Datenbanksysteme");

        student.setBesuchteVeranstaltungen(eingaben);

        assertThat(student.getBesuchteVeranstaltungen())
                .extracting(Veranstaltung::name)
                .containsExactly("Datenbanksysteme");
    }

    @Test
    void setter_ersetzt_die_bisherigen_werte() {
        student.addInteresse("KI");
        student.setInteressen(Set.of("Datenbanken"));

        assertThat(student.getInteressen())
                .extracting(Fachgebiet::fachgebiet)
                .containsExactly("Datenbanken");
    }

    @Test
    void getter_liefert_unveraenderliche_kopie() {
        student.addVeranstaltung("Analysis");

        Set<Veranstaltung> kopie = student.getBesuchteVeranstaltungen();

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> kopie.add(new Veranstaltung("Hack")))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
