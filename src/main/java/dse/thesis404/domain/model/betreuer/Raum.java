package dse.thesis404.domain.model.betreuer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Raum(int bereich, int gebaeude, int etage, String raumNummer) {

    private static final Pattern P =
            Pattern.compile("^(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,2})\\.(\\d{1,4})$");

    public Raum {
        if (bereich < 0 || bereich > 99) throw new IllegalArgumentException("Bereich muss 0..99 sein.");
        if (gebaeude < 0 || gebaeude > 99) throw new IllegalArgumentException("Gebäude muss 0..99 sein.");
        if (etage < 0 || etage > 99) throw new IllegalArgumentException("Etage muss 0..99 sein.");

        if (raumNummer == null || raumNummer.isBlank())
            throw new IllegalArgumentException("Raum-Nummer darf nicht leer sein!");

        if (!raumNummer.matches("\\d{1,4}"))
            throw new IllegalArgumentException("Raum-Nummer muss aus 1 bis 4 Ziffern bestehen.");

        raumNummer = normalizeRoomNumber(raumNummer);
    }

    public static Raum of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Raum-Nummer fehlt.");
        }

        String s = raw.trim();
        Matcher m = P.matcher(s);
        if (!m.matches()) {
            throw new IllegalArgumentException("Format ungültig. Erwartet z.B.: 25.12.02.03");
        }

        int bereich = Integer.parseInt(m.group(1));
        int gebaeude = Integer.parseInt(m.group(2));
        int etage = Integer.parseInt(m.group(3));
        String raumNr = m.group(4);

        return new Raum(bereich, gebaeude, etage, raumNr);
    }

    public String toCode(){
        return String.format("%02d.%02d.%02d.%s", bereich, gebaeude, etage, raumNummer);
    }

    private static String normalizeRoomNumber(String rn) {
        String trimmed = rn.trim();
        return (trimmed.length() == 1) ? "0" + trimmed : trimmed;
    }
}
