package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.relational.core.mapping.Table;

@Table("student_veranstaltung")
public class StudentVeranstaltung {

    private String veranstaltung;

    public StudentVeranstaltung() {}

    public StudentVeranstaltung(String veranstaltung) {
        this.veranstaltung = veranstaltung;
    }

    public String getVeranstaltung() {
        return veranstaltung;
    }

    public void setVeranstaltung(String veranstaltung) {
        this.veranstaltung = veranstaltung;
    }
}
