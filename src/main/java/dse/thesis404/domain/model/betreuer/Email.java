package dse.thesis404.domain.model.betreuer;

import java.util.regex.Pattern;

public record Email(String email) {
    

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    public Email {
        // Erst auf null/leer prüfen, dann trimmen – sonst NullPointerException statt klarer Meldung.
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email darf nicht leer sein!");
        }
        email = email.trim();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Ungültige Email-Adresse: " + email);
        }
    }

    @Override
    public String toString(){
        return email;
    }
}
