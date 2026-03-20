package dse.thesis404.infrastructure.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElement(NoSuchElementException ex, RedirectAttributes ra) {
        log.warn("BetreuerProfil nicht gefunden: {}", ex.getMessage());
        ra.addFlashAttribute("error", "Betreuer-Profil nicht gefunden.");
        return "redirect:/admin";
    }

    @ExceptionHandler(SecurityException.class)
    public String handleSecurity(SecurityException ex, RedirectAttributes ra) {
        log.warn("Sicherheitsfehler: {}", ex.getMessage());
        ra.addFlashAttribute("error", ex.getMessage());
        return "redirect:/homestudi";
    }
}
