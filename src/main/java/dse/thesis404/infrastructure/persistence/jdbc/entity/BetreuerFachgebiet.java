package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.relational.core.mapping.Table;

@Table("betreuer_fachgebiet")
public class BetreuerFachgebiet {

    private String fachgebiet;

    public BetreuerFachgebiet() {}

    public BetreuerFachgebiet(String fachgebiet) {
        this.fachgebiet = fachgebiet;
    }

    public String getFachgebiet() {
        return fachgebiet;
    }

    public void setFachgebiet(String fachgebiet) {
        this.fachgebiet = fachgebiet;
    }
}
