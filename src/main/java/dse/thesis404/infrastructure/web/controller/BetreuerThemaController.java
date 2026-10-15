package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.application.service.ThemeService;
import dse.thesis404.domain.model.shared.Fachgebiet;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.domain.model.theme.Veranstaltung;
import dse.thesis404.infrastructure.web.form.FachgebietForm;
import dse.thesis404.infrastructure.web.form.ThemeForm;
import dse.thesis404.infrastructure.web.support.AktuellerBetreuer;
import dse.thesis404.infrastructure.web.support.Auswahlkatalog;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashSet;
import java.util.stream.Collectors;

/** Themen eines Betreuers: anlegen, ansehen, bearbeiten, löschen. Besitzrechte prüft der ThemeService. */
@Controller
@RequestMapping("/betreuerProfil")
@SessionAttributes("themeForm")
public class BetreuerThemaController {

    private final ThemeService themeService;
    private final AktuellerBetreuer aktuellerBetreuer;
    private final Auswahlkatalog katalog;

    public BetreuerThemaController(ThemeService themeService, AktuellerBetreuer aktuellerBetreuer, Auswahlkatalog katalog) {
        this.themeService = themeService;
        this.aktuellerBetreuer = aktuellerBetreuer;
        this.katalog = katalog;
    }

    private void auswahllistenHinzufuegen(Model model) {
        model.addAttribute("allFachgebiete", katalog.fachgebiete());
        model.addAttribute("allVeranstaltungen", katalog.veranstaltungen());
        model.addAttribute("requiredModuleForm", new FachgebietForm());
    }

    // ---------- Neues Thema ----------

    @GetMapping("/themes/new")
    public String addThemeForm(Model model) {
        model.addAttribute("themeForm", new ThemeForm());
        auswahllistenHinzufuegen(model);
        return "addTopics";
    }

    @PostMapping("/themes")
    public String addThemeSubmit(
            @Valid @ModelAttribute("themeForm") ThemeForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            auswahllistenHinzufuegen(model);
            return "addTopics";
        }
        themeService.createTheme(
                aktuellerBetreuer.profileId(principal),
                form.getTitel(),
                form.getBeschreibungMarkdown(),
                form.getFachgebiete(),
                form.getRequiredModules()
        );
        ra.addFlashAttribute("success", "Neues Thema wurde erstellt.");
        return "redirect:/betreuerProfil";
    }

    @PostMapping("/themes/new/addVeranstaltung")
    public String addVeranstaltungToNewTheme(
            @Valid @ModelAttribute("requiredModuleForm") FachgebietForm form,
            BindingResult bindingResult,
            @ModelAttribute("themeForm") ThemeForm themeForm,
            RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Bitte wählen Sie eine Veranstaltung aus.");
            return "redirect:/betreuerProfil/themes/new";
        }
        if (themeForm.getRequiredModules() == null) {
            themeForm.setRequiredModules(new HashSet<>());
        }
        themeForm.getRequiredModules().add(form.getSelectedFachgebiet());
        ra.addFlashAttribute("success", "Veranstaltung wurde hinzugefügt.");
        return "redirect:/betreuerProfil/themes/new";
    }

    @PostMapping("/themes/new/deleteVeranstaltung")
    public String deleteVeranstaltungFromNewTheme(
            @RequestParam("veranstaltungToDelete") String veranstaltungToDelete,
            @ModelAttribute("themeForm") ThemeForm themeForm,
            RedirectAttributes ra
    ) {
        if (veranstaltungToDelete == null || veranstaltungToDelete.isBlank()) {
            ra.addFlashAttribute("error", "Ungültige Veranstaltung.");
            return "redirect:/betreuerProfil/themes/new";
        }
        if (themeForm.getRequiredModules() != null) {
            themeForm.getRequiredModules().remove(veranstaltungToDelete);
        }
        ra.addFlashAttribute("success", "Veranstaltung wurde entfernt.");
        return "redirect:/betreuerProfil/themes/new";
    }

    // ---------- Bestehendes Thema ----------

    @GetMapping("/theme/{id}")
    public String viewTheme(@PathVariable("id") String rawThemeId, Principal principal, Model model) {
        Theme theme = themeService.getOwnedTheme(ThemeId.parse(rawThemeId), aktuellerBetreuer.profileId(principal));
        model.addAttribute("theme", theme);
        return "theme";
    }

    @GetMapping("/editTheme/{id}")
    public String editThemeForm(@PathVariable("id") String rawThemeId, Principal principal, Model model) {
        Theme theme = themeService.getOwnedTheme(ThemeId.parse(rawThemeId), aktuellerBetreuer.profileId(principal));

        ThemeForm form = new ThemeForm();
        form.setTitel(theme.getTitel());
        form.setVersion(theme.getVersion());
        if (theme.getBeschreibung() != null) {
            form.setBeschreibungMarkdown(theme.getBeschreibung().markdown());
        }
        form.setFachgebiete(theme.getFachgebiete().stream()
                .map(Fachgebiet::fachgebiet)
                .collect(Collectors.toSet()));
        form.setRequiredModules(theme.getAngeforderteVeranstaltungen().stream()
                .map(Veranstaltung::name)
                .collect(Collectors.toSet()));

        model.addAttribute("themeForm", form);
        model.addAttribute("theme", theme);
        model.addAttribute("themeId", rawThemeId);
        auswahllistenHinzufuegen(model);
        return "editTheme";
    }

    @PostMapping("/editTheme/{id}")
    public String editThemeSubmit(
            @PathVariable("id") String rawThemeId,
            @Valid @ModelAttribute("themeForm") ThemeForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model,
            RedirectAttributes ra
    ) {
        ProfileId myId = aktuellerBetreuer.profileId(principal);
        ThemeId themeId = ThemeId.parse(rawThemeId);

        if (bindingResult.hasErrors()) {
            model.addAttribute("themeId", rawThemeId);
            model.addAttribute("theme", themeService.getOwnedTheme(themeId, myId));
            auswahllistenHinzufuegen(model);
            return "editTheme";
        }

        themeService.updateThema(themeId, myId, form.getTitel(), form.getBeschreibungMarkdown(), form.getVersion());
        ra.addFlashAttribute("success", "Thema wurde aktualisiert.");
        return "redirect:/betreuerProfil";
    }

    @PostMapping("/editTheme/{id}/addVeranstaltung")
    public String addVeranstaltungToTheme(
            @PathVariable("id") String rawThemeId,
            @Valid @ModelAttribute("requiredModuleForm") FachgebietForm form,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Bitte wählen Sie eine Veranstaltung aus.");
            return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
        }
        themeService.addVeranstaltung(ThemeId.parse(rawThemeId), aktuellerBetreuer.profileId(principal),
                form.getSelectedFachgebiet());
        ra.addFlashAttribute("success", "Veranstaltung wurde hinzugefügt.");
        return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
    }

    @PostMapping("/editTheme/{id}/deleteVeranstaltung")
    public String deleteVeranstaltungFromTheme(
            @PathVariable("id") String rawThemeId,
            @RequestParam("veranstaltungToDelete") String veranstaltungToDelete,
            Principal principal,
            RedirectAttributes ra
    ) {
        if (veranstaltungToDelete == null || veranstaltungToDelete.isBlank()) {
            ra.addFlashAttribute("error", "Ungültige Veranstaltung.");
            return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
        }
        themeService.removeVeranstaltung(ThemeId.parse(rawThemeId), aktuellerBetreuer.profileId(principal),
                veranstaltungToDelete);
        ra.addFlashAttribute("success", "Veranstaltung wurde entfernt.");
        return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
    }

    @PostMapping("/deleteTheme/{id}")
    public String deleteTheme(@PathVariable("id") String rawThemeId, Principal principal, RedirectAttributes ra) {
        themeService.deleteTheme(ThemeId.parse(rawThemeId), aktuellerBetreuer.profileId(principal));
        ra.addFlashAttribute("success", "Thema wurde gelöscht.");
        return "redirect:/betreuerProfil";
    }
}
