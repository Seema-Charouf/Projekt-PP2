package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.application.exception.NichtGefundenException;
import dse.thesis404.application.service.BetreuerProfilService;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.infrastructure.web.support.AktuellerBetreuer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** Anfragen von Studierenden zu eigenen Themen annehmen oder ablehnen. */
@Controller
@RequestMapping("/betreuerProfil")
public class BetreuerAnfrageController {

    private final BetreuerProfilService betreuerProfilService;
    private final AktuellerBetreuer aktuellerBetreuer;

    public BetreuerAnfrageController(BetreuerProfilService betreuerProfilService, AktuellerBetreuer aktuellerBetreuer) {
        this.betreuerProfilService = betreuerProfilService;
        this.aktuellerBetreuer = aktuellerBetreuer;
    }

    @PostMapping("/acceptTheme/{id}")
    public String acceptTheme(@PathVariable("id") String requestId, Principal principal, RedirectAttributes ra) {
        return acceptOrDecline(requestId, principal, ra, true);
    }

    @PostMapping("/declineTheme/{id}")
    public String declineTheme(@PathVariable("id") String requestId, Principal principal, RedirectAttributes ra) {
        return acceptOrDecline(requestId, principal, ra, false);
    }

    /** Anfrage-ID hat das Format "studentGithubId:themeUuid". */
    private String acceptOrDecline(String requestId, Principal principal, RedirectAttributes ra, boolean accept) {
        ProfileId studentId;
        ThemeId themeId;
        try {
            String[] parts = requestId.split(":", 2);
            studentId = ProfileId.ofGithubUserId(Long.parseLong(parts[0].trim()));
            themeId = ThemeId.parse(parts[1].trim());
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "Ungültige Anfrage-ID.");
            return "redirect:/betreuerProfil";
        }

        ProfileId myId = aktuellerBetreuer.profileId(principal);
        try {
            if (accept) {
                betreuerProfilService.acceptRequest(myId, studentId, themeId);
                ra.addFlashAttribute("info", "Anfrage angenommen.");
            } else {
                betreuerProfilService.declineRequest(myId, studentId, themeId);
                ra.addFlashAttribute("info", "Anfrage abgelehnt.");
            }
        } catch (NichtGefundenException e) {
            ra.addFlashAttribute("error", "Diese Anfrage existiert nicht (mehr).");
        }
        // ZugriffVerweigertException (fremdes Thema) wird bewusst NICHT gefangen → 403 über GlobalExceptionHandler
        return "redirect:/betreuerProfil";
    }
}
