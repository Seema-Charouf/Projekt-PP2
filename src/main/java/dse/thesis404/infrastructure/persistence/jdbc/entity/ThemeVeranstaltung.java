package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.relational.core.mapping.Table;

@Table("theme_veranstaltung")
public class ThemeVeranstaltung {

    private String name;

    public ThemeVeranstaltung() {}

    public ThemeVeranstaltung(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
