package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.relational.core.mapping.Table;

@Table("student_fachgebiet")
public class StudentFachgebiet {

    private String fachgebiet;

    public StudentFachgebiet() {}

    public StudentFachgebiet(String fachgebiet) {
        this.fachgebiet = fachgebiet;
    }

    public String getFachgebiet() {
        return fachgebiet;
    }

    public void setFachgebiet(String fachgebiet) {
        this.fachgebiet = fachgebiet;
    }
}
