package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.application.service.BetreuerProfilService;
import dse.thesis404.application.service.DateiViewService;
import dse.thesis404.application.service.ThemeService;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.betreuer.Titel;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.Fachgebiet;
import dse.thesis404.domain.model.shared.FileId;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.domain.model.theme.Veranstaltung;
import dse.thesis404.application.service.StudentThemeRequestView;
import dse.thesis404.infrastructure.config.security.OAuth2PrincipalUtils;
import dse.thesis404.infrastructure.web.converter.MultipartFileToCommandConverter;
import dse.thesis404.infrastructure.web.form.ThemeForm;
import dse.thesis404.infrastructure.web.form.DocumentForm;
import dse.thesis404.infrastructure.web.form.EditProfileForm;
import dse.thesis404.infrastructure.web.form.FachgebietForm;
import jakarta.validation.Valid;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/betreuerProfil")
@org.springframework.web.bind.annotation.SessionAttributes("themeForm")
public class BetreuerProfilController {

    private final BetreuerProfilService betreuerProfilService;
    private final ThemeService themeService;
    private final ThemeRepository themeRepository;
    private final MultipartFileToCommandConverter fileConverter;
    private final DateiViewService dateiViewService;

    private static final List<String> ALL_FACHGEBIETE = List.of(
            "Software Engineering",
            "Künstliche Intelligenz",
            "Datenbanken",
            "IT-Sicherheit",
            "Verteilte Systeme"
    );

    private static final List<Veranstaltung> ALL_VERANSTALTUNGEN = List.of(
            new Veranstaltung("Programmierung"),
            new Veranstaltung("Algorithmen und Datenstrukturen"),
            new Veranstaltung("Theoretische Informatik"),
            new Veranstaltung("Softwaretechnik"),
            new Veranstaltung("Datenbanksysteme"),
            new Veranstaltung("Rechnernetze"),
            new Veranstaltung("Betriebssysteme"),
            new Veranstaltung("Propra 1"),
            new Veranstaltung("Propra 2"),
            new Veranstaltung("Lineare Algebra"),
            new Veranstaltung("Analysis")
    );

    public BetreuerProfilController(
            BetreuerProfilService betreuerProfilService,
            ThemeService themeService,
            ThemeRepository themeRepository,
            MultipartFileToCommandConverter fileConverter,
            DateiViewService dateiViewService
    ) {
        this.betreuerProfilService = betreuerProfilService;
        this.themeService = themeService;
        this.themeRepository = themeRepository;
        this.fileConverter = fileConverter;
        this.dateiViewService = dateiViewService;
    }

    /**
     * Ermittelt das Betreuerprofil ausschließlich aus dem echten Login (GitHub-ID).
     * Es gibt bewusst keinen Weg, die Identität per Request-Parameter zu überschreiben.
     */
    private ProfileId currentProfileId(Principal principal) {
        if (principal == null) {
            throw new SecurityException("Nicht authentifiziert.");
        }

        Long githubId = OAuth2PrincipalUtils.extractGitHubId(principal).orElse(null);
        if (githubId == null) {
            throw new SecurityException("Keine gültige GitHub-ID.");
        }
        return betreuerProfilService.findByGithubId(githubId)
                .map(BetreuerProfil::getId)
                .orElseThrow(() -> new SecurityException(
                        "Du bist kein Betreuer. Es existiert kein Betreuerprofil für deine GitHub-ID. " +
                        "Bitte wende dich an einen Administrator."));
    }

    private ThemeId toThemeId(String raw) {
        return ThemeId.parse(raw);
    }

    private static FileId toFileId(String raw) {
        return FileId.parse(raw);
    }

    @GetMapping
    public String showBetreuerHome(Model model, Principal principal) {
        ProfileId myId = currentProfileId(principal);

        BetreuerProfil profil = betreuerProfilService.getById(myId);

        List<Theme> myThemes = themeRepository.findByOwnerProfileId(myId);

        List<Datei> documents = profil.getFiles();

        List<StudentThemeRequestVm> requests = betreuerProfilService.getRequestsForBetreuer(myId).stream()
                .map(v -> new StudentThemeRequestVm(
                        v.studentId().value() + ":" + v.themeId().value(),
                        v.themeTitle(),
                        v.studentDisplayName(),
                        v.status() == dse.thesis404.domain.model.student.RequestStatus.ACCEPTED
                ))
                .toList();

        model.addAttribute("myBetreuerProfile", profil);
        model.addAttribute("themes", myThemes);
        model.addAttribute("documents", documents);
        model.addAttribute("allFachgebiete", ALL_FACHGEBIETE);
        model.addAttribute("requests", requests);

        model.addAttribute("fachgebietForm", new FachgebietForm());
        model.addAttribute("documentForm", new DocumentForm());

        return "betreuerProfil";
    }

    @GetMapping("/editProfile")
    public String editProfileForm(Model model, Principal principal) {
        ProfileId myId = currentProfileId(principal);
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
        ProfileId myId = currentProfileId(principal);

        if (bindingResult.hasErrors()) {
            BetreuerProfil profil = betreuerProfilService.getById(myId);
            model.addAttribute("myBetreuerProfile", profil);
            model.addAttribute("titelOptions", Titel.values());
            return "editProfile";
        }

        if (form.getTitel() != null && !form.getTitel().isBlank()) {
            try {
                Titel titel = Titel.valueOf(form.getTitel());
                betreuerProfilService.updateTitel(myId, titel);
            } catch (IllegalArgumentException ignored) {

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

    @GetMapping("/themes/new")
    public String addThemeForm(Model model) {
        model.addAttribute("themeForm", new ThemeForm());
        model.addAttribute("allFachgebiete", ALL_FACHGEBIETE);
        model.addAttribute("allVeranstaltungen", ALL_VERANSTALTUNGEN);
        model.addAttribute("requiredModuleForm", new FachgebietForm());
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
            model.addAttribute("allFachgebiete", ALL_FACHGEBIETE);
            model.addAttribute("allVeranstaltungen", ALL_VERANSTALTUNGEN);
            model.addAttribute("requiredModuleForm", new FachgebietForm());
            return "addTopics";
        }

        ProfileId myId = currentProfileId(principal);

        themeService.createTheme(
                myId, 
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
            Model model,
            RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Bitte wählen Sie eine Veranstaltung aus.");
            return "redirect:/betreuerProfil/themes/new";
        }

        if (themeForm.getRequiredModules() == null) {
            themeForm.setRequiredModules(new java.util.HashSet<>());
        }
        themeForm.getRequiredModules().add(form.getSelectedFachgebiet());
        
        model.addAttribute("themeForm", themeForm);
        model.addAttribute("allFachgebiete", ALL_FACHGEBIETE);
        model.addAttribute("allVeranstaltungen", ALL_VERANSTALTUNGEN);
        model.addAttribute("requiredModuleForm", new FachgebietForm());
        
        ra.addFlashAttribute("success", "Veranstaltung wurde hinzugefügt.");
        return "redirect:/betreuerProfil/themes/new";
    }

    @PostMapping("/themes/new/deleteVeranstaltung")
    public String deleteVeranstaltungFromNewThemePost(
            @RequestParam("veranstaltungToDelete") String veranstaltungToDelete,
            @ModelAttribute("themeForm") ThemeForm themeForm,
            Model model,
            RedirectAttributes ra
    ) {
        return deleteVeranstaltungFromNewThemeInternal(veranstaltungToDelete, themeForm, model, ra);
    }


    private String deleteVeranstaltungFromNewThemeInternal(String veranstaltungToDelete, ThemeForm themeForm, Model model, RedirectAttributes ra) {
        if (veranstaltungToDelete == null || veranstaltungToDelete.isBlank()) {
            ra.addFlashAttribute("error", "Ungültige Veranstaltung.");
            return "redirect:/betreuerProfil/themes/new";
        }

        if (themeForm.getRequiredModules() != null) {
            themeForm.getRequiredModules().remove(veranstaltungToDelete);
        }
        
        model.addAttribute("themeForm", themeForm);
        model.addAttribute("allFachgebiete", ALL_FACHGEBIETE);
        model.addAttribute("allVeranstaltungen", ALL_VERANSTALTUNGEN);
        model.addAttribute("requiredModuleForm", new FachgebietForm());
        
        ra.addFlashAttribute("success", "Veranstaltung wurde entfernt.");
        return "redirect:/betreuerProfil/themes/new";
    }

    @GetMapping("/editTheme/{id}")
    public String editThemeForm(@PathVariable("id") String rawThemeId, Principal principal, Model model) {
        ProfileId myId = currentProfileId(principal);
        ThemeId themeId = toThemeId(rawThemeId);

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("Theme nicht gefunden: " + rawThemeId));

        if (!theme.getOwnerProfileId().equals(myId)) {
            throw new SecurityException("Kein Zugriff: Theme gehört nicht zu diesem Profil.");
        }

        ThemeForm form = new ThemeForm();
        form.setTitel(theme.getTitel());
        if (theme.getBeschreibung() != null) {
            form.setBeschreibungMarkdown(theme.getBeschreibung().markdown());
        }
        

        form.setFachgebiete(theme.getFachgebiete().stream()
                .map(Fachgebiet::fachgebiet)
                .collect(java.util.stream.Collectors.toSet()));
        form.setRequiredModules(theme.getAngeforderteVeranstaltungen().stream()
                .map(Veranstaltung::name)
                .collect(java.util.stream.Collectors.toSet()));

        model.addAttribute("themeForm", form);
        model.addAttribute("theme", theme);
        model.addAttribute("themeId", rawThemeId);
        model.addAttribute("allFachgebiete", ALL_FACHGEBIETE);
        model.addAttribute("allVeranstaltungen", ALL_VERANSTALTUNGEN);
        model.addAttribute("requiredModuleForm", new FachgebietForm());
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
        ProfileId myId = currentProfileId(principal);
        ThemeId themeId = toThemeId(rawThemeId);

        if (bindingResult.hasErrors()) {

            model.addAttribute("themeId", rawThemeId);
            model.addAttribute("allFachgebiete", ALL_FACHGEBIETE);
            model.addAttribute("allVeranstaltungen", ALL_VERANSTALTUNGEN);
            model.addAttribute("requiredModuleForm", new FachgebietForm());

            Theme theme = themeRepository.findById(themeId)
                    .orElseThrow(() -> new NoSuchElementException("Theme nicht gefunden: " + rawThemeId));
            model.addAttribute("theme", theme);
            return "editTheme";
        }

        themeService.updateTitel(themeId, myId, form.getTitel());
        themeService.updateBeschreibung(themeId, myId, form.getBeschreibungMarkdown());

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
        ProfileId myId = currentProfileId(principal);
        ThemeId themeId = toThemeId(rawThemeId);

        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Bitte wählen Sie eine Veranstaltung aus.");
            return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
        }

        themeService.addVeranstaltung(themeId, myId, form.getSelectedFachgebiet());
        ra.addFlashAttribute("success", "Veranstaltung wurde hinzugefügt.");
        return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
    }

    @PostMapping("/editTheme/{id}/deleteVeranstaltung")
    public String deleteVeranstaltungFromThemePost(
            @PathVariable("id") String rawThemeId,
            @RequestParam("veranstaltungToDelete") String veranstaltungToDelete,
            Principal principal,
            RedirectAttributes ra
    ) {
        return deleteVeranstaltungFromThemeInternal(rawThemeId, veranstaltungToDelete, principal, ra);
    }


    private String deleteVeranstaltungFromThemeInternal(String rawThemeId, String veranstaltungToDelete, Principal principal, RedirectAttributes ra) {
        if (veranstaltungToDelete == null || veranstaltungToDelete.isBlank()) {
            ra.addFlashAttribute("error", "Ungültige Veranstaltung.");
            return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
        }

        ProfileId myId = currentProfileId(principal);
        ThemeId themeId = toThemeId(rawThemeId);
        themeService.removeVeranstaltung(themeId, myId, veranstaltungToDelete);
        ra.addFlashAttribute("success", "Veranstaltung wurde entfernt.");
        return "redirect:/betreuerProfil/editTheme/" + rawThemeId;
    }

    @PostMapping("/deleteTheme/{id}")
    public String deleteThemePost(@PathVariable("id") String rawThemeId, Principal principal, RedirectAttributes ra) {
        return deleteThemeInternal(rawThemeId, principal, ra);
    }


    private String deleteThemeInternal(String rawThemeId, Principal principal, RedirectAttributes ra) {
        ProfileId myId = currentProfileId(principal);
        ThemeId themeId = toThemeId(rawThemeId);
        themeService.deleteTheme(themeId, myId);
        ra.addFlashAttribute("success", "Thema wurde gelöscht.");
        return "redirect:/betreuerProfil";
    }

    @GetMapping("/theme/{id}")
    public String viewTheme(@PathVariable("id") String rawThemeId, Principal principal, Model model) {
        ProfileId myId = currentProfileId(principal);
        ThemeId themeId = toThemeId(rawThemeId);

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new NoSuchElementException("Theme nicht gefunden: " + rawThemeId));

        if (!theme.getOwnerProfileId().equals(myId)) {
            throw new SecurityException("Kein Zugriff: Theme gehört nicht zu diesem Profil.");
        }

        model.addAttribute("theme", theme);

        return "theme";
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

        ProfileId myId = currentProfileId(principal);
        betreuerProfilService.addFachgebiet(myId, form.getSelectedFachgebiet());
        ra.addFlashAttribute("success", "Fachgebiet wurde hinzugefügt.");
        return "redirect:/betreuerProfil";
    }

    @PostMapping("/deleteFachgebiet")
    public String deleteFachgebietPost(@RequestParam("fachgebietToDelete") String fachgebietKey, Principal principal, RedirectAttributes ra) {
        return deleteFachgebietInternal(fachgebietKey, principal, ra);
    }


    private String deleteFachgebietInternal(String fachgebietKey, Principal principal, RedirectAttributes ra) {
        if (fachgebietKey == null || fachgebietKey.isBlank()) {
            ra.addFlashAttribute("error", "Ungültiges Fachgebiet.");
            return "redirect:/betreuerProfil";
        }

        ProfileId myId = currentProfileId(principal);
        betreuerProfilService.deleteFachgebietByKey(myId, fachgebietKey);
        ra.addFlashAttribute("success", "Fachgebiet wurde entfernt.");
        return "redirect:/betreuerProfil";
    }

    @PostMapping("/uploadDocument")
    public String uploadDocument(
            @Valid @ModelAttribute("documentForm") DocumentForm form,
            BindingResult bindingResult,
            Principal principal,
            RedirectAttributes ra
    ) {
        if (bindingResult.hasErrors()) {
            ra.addFlashAttribute("error", "Bitte füllen Sie alle erforderlichen Felder aus.");
            return "redirect:/betreuerProfil";
        }

        if (form.getFile() == null || form.getFile().isEmpty()) {
            ra.addFlashAttribute("error", "Bitte wählen Sie eine Datei zum Hochladen aus.");
            return "redirect:/betreuerProfil";
        }

        ProfileId myId = currentProfileId(principal);
        String uploaderId = String.valueOf(myId.value());

        try {
            UploadInfoFileCommand cmd = fileConverter.convert(
                    form.getFile(),
                    form.getTitle(),
                    form.getDescription()
            );
            betreuerProfilService.uploadInfoFile(myId, uploaderId, cmd);
            ra.addFlashAttribute("success", "Dokument wurde hochgeladen.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Fehler beim Hochladen: " + e.getMessage());
        }

        return "redirect:/betreuerProfil";
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable("id") String rawFileId, Principal principal) {
        ProfileId myId = currentProfileId(principal);
        FileId fileId = toFileId(rawFileId);

        BetreuerProfil profil = betreuerProfilService.getById(myId);
        Datei datei = profil.getFiles().stream()
                .filter(f -> f.id().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Datei nicht gefunden: " + rawFileId));

        var result = dateiViewService.loadForDownload(datei);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, result.contentType());
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(result.filename(), StandardCharsets.UTF_8)
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(result.bytes());
    }

    @PostMapping("/deleteDocument/{id}")
    public String deleteDocumentPost(@PathVariable("id") String rawFileId, Principal principal, RedirectAttributes ra) {
        return deleteDocumentInternal(rawFileId, principal, ra);
    }


    private String deleteDocumentInternal(String rawFileId, Principal principal, RedirectAttributes ra) {
        if (rawFileId == null || rawFileId.isBlank()) {
            ra.addFlashAttribute("error", "Ungültige Datei-ID.");
            return "redirect:/betreuerProfil";
        }

        ProfileId myId = currentProfileId(principal);
        FileId fileId = toFileId(rawFileId);

        betreuerProfilService.deleteFile(myId, fileId);
        ra.addFlashAttribute("success", "Dokument wurde gelöscht.");
        return "redirect:/betreuerProfil";
    }

    @PostMapping("/acceptTheme/{id}")
    public String acceptTheme(@PathVariable("id") String requestId, Principal principal, RedirectAttributes ra) {
        return acceptOrDecline(requestId, principal, ra, true);
    }

    @PostMapping("/declineTheme/{id}")
    public String declineTheme(@PathVariable("id") String requestId, Principal principal, RedirectAttributes ra) {
        return acceptOrDecline(requestId, principal, ra, false);
    }

    private String acceptOrDecline(String requestId, Principal principal, RedirectAttributes ra, boolean accept) {
        if (requestId == null || requestId.isBlank()) {
            ra.addFlashAttribute("error", "Ungültige Anfrage-ID.");
            return "redirect:/betreuerProfil";
        }
        String[] parts = requestId.split(":", 2);
        if (parts.length != 2) {
            ra.addFlashAttribute("error", "Ungültige Anfrage-ID.");
            return "redirect:/betreuerProfil";
        }
        long studentIdValue;
        try {
            studentIdValue = Long.parseLong(parts[0].trim());
        } catch (NumberFormatException e) {
            ra.addFlashAttribute("error", "Ungültige Anfrage-ID.");
            return "redirect:/betreuerProfil";
        }
        ThemeId themeId;
        try {
            themeId = ThemeId.parse(parts[1].trim());
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", "Ungültige Anfrage-ID.");
            return "redirect:/betreuerProfil";
        }
        ProfileId myId = currentProfileId(principal);
        ProfileId studentId = ProfileId.ofGithubUserId(studentIdValue);
        try {
            if (accept) {
                betreuerProfilService.acceptRequest(myId, studentId, themeId);
                ra.addFlashAttribute("info", "Anfrage angenommen.");
            } else {
                betreuerProfilService.declineRequest(myId, studentId, themeId);
                ra.addFlashAttribute("info", "Anfrage abgelehnt.");
            }
        } catch (IllegalArgumentException | NoSuchElementException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/betreuerProfil";
    }


    
    public record StudentThemeRequestVm(
            String id,
            String themeTitle,
            String studentDisplayName,
            boolean accepted
    ) {}
}
