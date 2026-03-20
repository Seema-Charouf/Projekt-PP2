package dse.thesis404.domain.model.shared;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public record Link(URL url, String text) {
    
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
            URI uri = new URI(urlString);
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
