package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.application.service.BetreuerProfilService;
import dse.thesis404.application.service.ThemeService;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.betreuer.Titel;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.infrastructure.web.form.DocumentForm;
import dse.thesis404.infrastructure.web.form.EditProfileForm;
import dse.thesis404.infrastructure.web.form.FachgebietForm;
import dse.thesis404.infrastructure.web.support.AktuellerBetreuer;
import dse.thesis404.infrastructure.web.support.Auswahlkatalog;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

/** Startseite des Betreuers und Pflege des eigenen Profils (Stammdaten, Fachgebiete). */
@Controller
@RequestMapping("/betreuerProfil")
public class BetreuerProfilController {

    private final BetreuerProfilService betreuerProfilService;
    private final ThemeService themeService;
    private final AktuellerBetreuer aktuellerBetreuer;
    private final Auswahlkatalog katalog;

    public BetreuerProfilController(
            BetreuerProfilService betreuerProfilService,
            ThemeService themeService,
            AktuellerBetreuer aktuellerBetreuer,
            Auswahlkatalog katalog
    ) {
        this.betreuerProfilService = betreuerProfilService;
        this.themeService = themeService;
        this.aktuellerBetreuer = aktuellerBetreuer;
        this.katalog = katalog;
    }

    @GetMapping
    public String showBetreuerHome(Model model, Principal principal) {
        ProfileId myId = aktuellerBetreuer.profileId(principal);
        BetreuerProfil profil = betreuerProfilService.getById(myId);

        List<StudentThemeRequestVm> requests = betreuerProfilService.getRequestsForBetreuer(myId).stream()
                .map(v -> new StudentThemeRequestVm(
                        v.studentId().value() + ":" + v.themeId().value(),
                        v.themeTitle(),
                        v.studentDisplayName(),
                        v.status() == RequestStatus.ACCEPTED
                ))
                .toList();

        model.addAttribute("myBetreuerProfile", profil);
        model.addAttribute("themes", themeService.findByOwner(myId));
        model.addAttribute("documents", profil.getFiles());
        model.addAttribute("allFachgebiete", katalog.fachgebiete());
        model.addAttribute("requests", requests);
        model.addAttribute("fachgebietForm", new FachgebietForm());
        model.addAttribute("documentForm", new DocumentForm());
        return "betreuerProfil";
    }

    @GetMapping("/editProfile")
    public String editProfileForm(Model model, Principal principal) {
        ProfileId myId = aktuellerBetreuer.profileId(principal);
        BetreuerProfil profil = betreuerProfilService.getById(myId);

        EditProfileForm form = new EditProfileForm();
        form.setTitel(profil.getTitel().name());
        form.setName(profil.getName());
        if (profil.getMail() != null) {
            form.setEmail(profil.getMail().email());
        }
        if (profil.getPhoneNumber() != null) {
            form.setPhone(profil.getPhoneNumber().phoneNumber());
        }
        if (profil.getRaum() != null) {
            form.setRaum(profil.getRaum().toCode());
        }

        model.addAttribute("editProfileForm", form);
        model.addAttribute("myBetreuerProfile", profil);
        model.addAttribute("titelOptions", Titel.values());
        return "editProfile";
    }

    @PostMapping("/editProfile")
    public String editProfileSubmit(
            @Valid @ModelAttribute("editProfileForm") EditProfileForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes ra
    ) {
        ProfileId myId = aktuellerBetreuer.profileId(principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute("myBetreuerProfile", betreuerProfilService.getById(myId));
            model.addAttribute("titelOptions", Titel.values());
            return "editProfile";
        }

        if (form.getTitel() != null && !form.getTitel().isBlank()) {
            try {
                betreuerProfilService.updateTitel(myId, Titel.valueOf(form.getTitel()));
            } catch (IllegalArgumentException ignored) {
                // unbekannter Titel aus dem Formular: Titel bleibt unverändert
            }
        }
        if (form.getName() != null && !form.getName().isBlank()) {
            betreuerProfilService.updateName(myId, form.getName().trim());
        }
        if (form.getEmail() != null && !form.getEmail().isBlank()) {
            betreuerProfilService.updateEmail(myId, form.getEmail().trim());
        }
        if (form.getPhone() != null && !form.getPhone().isBlank()) {
            betreuerProfilService.updatePhone(myId, form.getPhone().trim());
        }
        if (form.getRaum() != null && !form.getRaum().isBlank()) {
            betreuerProfilService.updateRaum(myId, form.getRaum().trim());
        }

        ra.addFlashAttribute("success", "Profil wurde erfolgreich aktualisiert.");
        return "redirect:/betreuerProfil";
    }

    @PostMapping("/addFachgebiet")
    public String addFachgebiet(
            @Valid @ModelAttribute("fachgebietForm") FachgebietForm form,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Bitte wählen Sie ein gültiges Fachgebiet aus.");
            return "redirect:/betreuerProfil";
        }
        betreuerProfilService.addFachgebiet(aktuellerBetreuer.profileId(principal), form.getSelectedFachgebiet());
        ra.addFlashAttribute("success", "Fachgebiet wurde hinzugefügt.");
        return "redirect:/betreuerProfil";
    }

    @PostMapping("/deleteFachgebiet")
    public String deleteFachgebiet(@RequestParam("fachgebietToDelete") String fachgebietKey,
                                   Principal principal, RedirectAttributes ra) {
        if (fachgebietKey == null || fachgebietKey.isBlank()) {
            ra.addFlashAttribute("error", "Ungültiges Fachgebiet.");
            return "redirect:/betreuerProfil";
        }
        betreuerProfilService.deleteFachgebietByKey(aktuellerBetreuer.profileId(principal), fachgebietKey);
        ra.addFlashAttribute("success", "Fachgebiet wurde entfernt.");
        return "redirect:/betreuerProfil";
    }

    public record StudentThemeRequestVm(
            String id,
            String themeTitle,
            String studentDisplayName,
            boolean accepted
    ) {}
}
