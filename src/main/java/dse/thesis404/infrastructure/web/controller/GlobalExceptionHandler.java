package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.application.exception.NichtGefundenException;
import dse.thesis404.application.exception.ZugriffVerweigertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.NoSuchElementException;

/**
 * Übersetzt fachliche Fehler in passende HTTP-Statuscodes und eine neutrale Fehlerseite.
 * Interne Details (IDs, Klassennamen, Gründe) landen nur im Log, nie beim Nutzer.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ZugriffVerweigertException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleZugriffVerweigert(ZugriffVerweigertException ex, Model model) {
        log.warn("Zugriff verweigert: {}", ex.getMessage());
        return fehlerseite(model, 403, "Kein Zugriff",
                "Du hast keine Berechtigung für diese Seite oder Aktion.");
    }

    @ExceptionHandler({NichtGefundenException.class, NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNichtGefunden(RuntimeException ex, Model model) {
        log.info("Nicht gefunden: {}", ex.getMessage());
        return fehlerseite(model, 404, "Nicht gefunden",
                "Das gesuchte Element existiert nicht (mehr).");
    }

    private static String fehlerseite(Model model, int status, String titel, String text) {
        model.addAttribute("status", status);
        model.addAttribute("titel", titel);
        model.addAttribute("text", text);
        return "fehler";
    }
}
