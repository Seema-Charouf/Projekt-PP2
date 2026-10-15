package dse.thesis404.application.exception;

/** Das angefragte Objekt (Thema, Profil, Datei …) existiert nicht. → HTTP 404 */
public class NichtGefundenException extends RuntimeException {
    public NichtGefundenException(String message) {
        super(message);
    }
}
