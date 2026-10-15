package dse.thesis404.application.exception;

/** Der eingeloggte Nutzer darf diese Aktion nicht ausführen (z. B. fremdes Thema bearbeiten). → HTTP 403 */
public class ZugriffVerweigertException extends RuntimeException {
    public ZugriffVerweigertException(String message) {
        super(message);
    }
}
