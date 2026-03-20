package dse.thesis404.infrastructure.persistence.jdbc.entity;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table("betreuer_link")
public class BetreuerLink {

    private String url;

    @Column("link_text")
    private String linkText;

    public BetreuerLink() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BetreuerLink that = (BetreuerLink) o;
        return Objects.equals(url, that.url) && Objects.equals(linkText, that.linkText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, linkText);
    }

    public BetreuerLink(String url, String linkText) {
        this.url = url;
        this.linkText = linkText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }
}
