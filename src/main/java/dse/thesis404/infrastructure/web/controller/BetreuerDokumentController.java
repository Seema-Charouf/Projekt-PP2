package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.application.service.BetreuerProfilService;
import dse.thesis404.application.service.DateiViewService;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.FileId;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.infrastructure.web.converter.MultipartFileToCommandConverter;
import dse.thesis404.infrastructure.web.form.DocumentForm;
import dse.thesis404.infrastructure.web.support.AktuellerBetreuer;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

/** Dokumente im Betreuerprofil: hochladen, herunterladen, löschen. */
@Controller
@RequestMapping("/betreuerProfil")
public class BetreuerDokumentController {

    private static final Logger log = LoggerFactory.getLogger(BetreuerDokumentController.class);

    private final BetreuerProfilService betreuerProfilService;
    private final DateiViewService dateiViewService;
    private final MultipartFileToCommandConverter fileConverter;
    private final AktuellerBetreuer aktuellerBetreuer;

    public BetreuerDokumentController(
            BetreuerProfilService betreuerProfilService,
            DateiViewService dateiViewService,
            MultipartFileToCommandConverter fileConverter,
            AktuellerBetreuer aktuellerBetreuer
    ) {
        this.betreuerProfilService = betreuerProfilService;
        this.dateiViewService = dateiViewService;
        this.fileConverter = fileConverter;
        this.aktuellerBetreuer = aktuellerBetreuer;
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

        ProfileId myId = aktuellerBetreuer.profileId(principal);
        try {
            UploadInfoFileCommand cmd = fileConverter.convert(form.getFile(), form.getTitle(), form.getDescription());
            betreuerProfilService.uploadInfoFile(myId, String.valueOf(myId.value()), cmd);
            ra.addFlashAttribute("success", "Dokument wurde hochgeladen.");
        } catch (IllegalArgumentException e) {
            // Validierungsfehler der Domain (z. B. "Nur PDF, ZIP oder MD") sind für Nutzer gedacht
            ra.addFlashAttribute("error", "Fehler beim Hochladen: " + e.getMessage());
        } catch (Exception e) {
            // Technische Fehler: Details nur ins Log, nicht an den Nutzer
            log.error("Upload fehlgeschlagen für Profil {}", myId, e);
            ra.addFlashAttribute("error", "Das Dokument konnte nicht gespeichert werden. Bitte später erneut versuchen.");
        }
        return "redirect:/betreuerProfil";
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable("id") String rawFileId, Principal principal) {
        Datei datei = betreuerProfilService.getEigeneDatei(aktuellerBetreuer.profileId(principal), FileId.parse(rawFileId));
        var result = dateiViewService.loadForDownload(datei);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, result.contentType());
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(result.filename(), StandardCharsets.UTF_8)
                .build());
        return ResponseEntity.ok().headers(headers).body(result.bytes());
    }

    @PostMapping("/deleteDocument/{id}")
    public String deleteDocument(@PathVariable("id") String rawFileId, Principal principal, RedirectAttributes ra) {
        if (rawFileId == null || rawFileId.isBlank()) {
            ra.addFlashAttribute("error", "Ungültige Datei-ID.");
            return "redirect:/betreuerProfil";
        }
        betreuerProfilService.deleteFile(aktuellerBetreuer.profileId(principal), FileId.parse(rawFileId));
        ra.addFlashAttribute("success", "Dokument wurde gelöscht.");
        return "redirect:/betreuerProfil";
    }
}
