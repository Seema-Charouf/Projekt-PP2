package dse.thesis404.application.exception;

/** Jemand anderes hat den Datensatz geändert, seit er geladen wurde (Optimistic Locking). → HTTP 409 */
public class KonfliktException extends RuntimeException {
    public KonfliktException(String message) {
        super(message);
    }
}
