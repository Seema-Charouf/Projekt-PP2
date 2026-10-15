package dse.thesis404.domain.model.shared;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Set;

public record Link(URL url, String text) {

    /** Bewusst nur Web-Links. Verhindert z. B. javascript:-, data:- oder file:-Links. */
    private static final Set<String> ERLAUBTE_SCHEMAS = Set.of("http", "https");

    public Link {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Link-Text darf nicht leer sein!");
        }
        if (url == null) {
            throw new IllegalArgumentException("URL darf nicht null sein!");
        }
    }
    

    public static Link fromTextToURL(String urlString, String text) {
        if (urlString == null || urlString.isBlank()) {
            throw new IllegalArgumentException("URL-String darf nicht leer sein!");
        }
        try {
            URI uri = new URI(urlString.trim());
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!ERLAUBTE_SCHEMAS.contains(scheme) || uri.getHost() == null) {
                throw new IllegalArgumentException("Nur http- und https-Links sind erlaubt: " + urlString);
            }
            URL url = uri.toURL();
            return new Link(url, text);
        } catch (URISyntaxException | java.net.MalformedURLException e) {
            throw new IllegalArgumentException("Ungültige URL: " + urlString, e);
        }
    }
    
    @Override
    public String toString() {
        return text;
    }
}
