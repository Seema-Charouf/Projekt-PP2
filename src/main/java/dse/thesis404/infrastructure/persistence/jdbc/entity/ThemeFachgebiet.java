package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.relational.core.mapping.Table;

@Table("theme_fachgebiet")
public class ThemeFachgebiet {

    private String fachgebiet;

    public ThemeFachgebiet() {}

    public ThemeFachgebiet(String fachgebiet) {
        this.fachgebiet = fachgebiet;
    }

    public String getFachgebiet() {
        return fachgebiet;
    }

    public void setFachgebiet(String fachgebiet) {
        this.fachgebiet = fachgebiet;
    }
}
