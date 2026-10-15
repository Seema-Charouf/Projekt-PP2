package dse.thesis404.application.service;

import dse.thesis404.application.exception.ZugriffVerweigertException;
import dse.thesis404.application.exception.NichtGefundenException;
import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.FileStoragePort;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.FileId;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;
    private final BetreuerProfilRepository betreuerProfilRepository;
    private final DateiUploadService dateiUploadService;

    public ThemeService(
            ThemeRepository themeRepository,
            BetreuerProfilRepository betreuerProfilRepository,
            DateiUploadService dateiUploadService
    ) {
        this.themeRepository = Objects.requireNonNull(themeRepository, "themeRepository");
        this.betreuerProfilRepository = Objects.requireNonNull(betreuerProfilRepository, "betreuerProfilRepository");
        this.dateiUploadService = Objects.requireNonNull(dateiUploadService, "dateiUploadService");
    }

    
    @Transactional
    public ThemeId createTheme(ProfileId ownerProfileId,
                               String titel,
                               String markdownBeschreibung,
                               Set<String> fachgebiete,
                               Set<String> veranstaltungen)
    {
        Objects.requireNonNull(ownerProfileId, "ownerProfileId");
        Objects.requireNonNull(titel, "titel");
        
        if (!betreuerProfilRepository.exists(ownerProfileId)) {
            throw new IllegalArgumentException("BetreuerProfil existiert nicht: " + ownerProfileId);
        }

        // Kein Inhaltsfilter: Die Beschreibung wird beim Anzeigen HTML-escaped (th:text).
        String md = markdownBeschreibung == null ? "" : markdownBeschreibung;

        Theme theme = new Theme(
                ThemeId.newId(),
                ownerProfileId,
                titel,
                new Beschreibung(md)
        );

        if (fachgebiete != null) {
            for (String fg : fachgebiete) {
                if (fg != null && !fg.isBlank()) {
                    theme.addFachgebiet(fg);
                }
            }
        }
        
        if (veranstaltungen != null) {
            for (String v : veranstaltungen) {
                if (v != null && !v.isBlank()) {
                    theme.addVeranstaltung(v);
                }
            }
        }

        themeRepository.save(theme);
        return theme.getId();
    }
    
    
    @Transactional
    public ThemeId createTheme(ProfileId ownerProfileId, String titel, String markdownBeschreibung) {
        return createTheme(ownerProfileId, titel, markdownBeschreibung, null, null);
    }

    @Transactional
    public void deleteTheme(ThemeId themeId, ProfileId actorProfileId) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        themeRepository.delete(theme.getId());
    }

    @Transactional
    public void updateTitel(ThemeId themeId, ProfileId actorProfileId, String neuerTitel) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        theme.updateTitel(neuerTitel);
        themeRepository.save(theme);
    }

    @Transactional
    public void updateBeschreibung(ThemeId themeId, ProfileId actorProfileId, String markdown) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);

        String md = markdown == null ? "" : markdown;

        theme.updateBeschreibung(md);
        themeRepository.save(theme);
    }

    @Transactional
    public void addVeranstaltung(ThemeId themeId, ProfileId actorProfileId, String veranstaltungName) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        theme.addVeranstaltung(veranstaltungName);
        themeRepository.save(theme);
    }

    @Transactional
    public void removeVeranstaltung(ThemeId themeId, ProfileId actorProfileId, String veranstaltungName) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        theme.deleteVeranstaltung(veranstaltungName);
        themeRepository.save(theme);
    }

    @Transactional
    public void addFachgebiet(ThemeId themeId, ProfileId actorProfileId, String fachgebietName) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        theme.addFachgebiet(fachgebietName);
        themeRepository.save(theme);
    }

    @Transactional
    public void removeFachgebiet(ThemeId themeId, ProfileId actorProfileId, String fachgebietName) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        theme.deleteFachgebiet(fachgebietName);
        themeRepository.save(theme);
    }

    @Transactional
    public void addLink(ThemeId themeId, ProfileId actorProfileId, String url, String text) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        theme.addLink(url, text);
        themeRepository.save(theme);
    }

    @Transactional
    public void removeLink(ThemeId themeId, ProfileId actorProfileId, String url, String text) {
        Theme theme = loadOwnedTheme(themeId, actorProfileId);
        theme.deleteLink(url, text);
        themeRepository.save(theme);
    }

    @Transactional
    public FileId uploadInfoFileToTheme(ThemeId themeId, ProfileId actorProfileId, UploadInfoFileCommand cmd) {
        Objects.requireNonNull(themeId, "themeId");
        Objects.requireNonNull(actorProfileId, "actorProfileId");
        Objects.requireNonNull(cmd, "cmd");
        
        Theme theme = loadOwnedTheme(themeId, actorProfileId);

        var result = dateiUploadService.storeAndCreateMeta(actorProfileId.toString(), cmd);
        Datei datei = result.meta();

        try {
            theme.addFile(datei);
            themeRepository.save(theme);
        } catch (RuntimeException e) {

            try {
                dateiUploadService.deleteFromStorage(datei);
            } catch (Exception ignore) {

            }
            throw e;
        }

        return datei.id();
    }

    @Transactional
    public void deleteFileFromTheme(ThemeId themeId, ProfileId actorProfileId, FileId fileId) {
        Objects.requireNonNull(themeId, "themeId");
        Objects.requireNonNull(actorProfileId, "actorProfileId");
        Objects.requireNonNull(fileId, "fileId");
        
        Theme theme = loadOwnedTheme(themeId, actorProfileId);

        Datei file = theme.getFiles().stream()
                .filter(f -> f.id().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new NichtGefundenException("Datei nicht gefunden: " + fileId));

        theme.deleteFile(fileId);
        themeRepository.save(theme);

        boolean deleted = dateiUploadService.deleteFromStorage(file);
        if (!deleted) {
            throw new FileStoragePort.FileStorageException(
                    "Datei konnte nicht aus dem Dateisystem gelöscht werden: " + file.storedFilename()
            );
        }
    }

    /** Alle Themen eines Betreuers (für seine Übersichtsseite). */
    @Transactional(readOnly = true)
    public List<Theme> findByOwner(ProfileId ownerProfileId) {
        Objects.requireNonNull(ownerProfileId, "ownerProfileId");
        return themeRepository.findByOwnerProfileId(ownerProfileId);
    }

    /** Lädt ein Thema nur, wenn es dem anfragenden Betreuer gehört (sonst 404 bzw. 403). */
    @Transactional(readOnly = true)
    public Theme getOwnedTheme(ThemeId themeId, ProfileId actorProfileId) {
        return loadOwnedTheme(themeId, actorProfileId);
    }

    private Theme loadOwnedTheme(ThemeId id, ProfileId actorProfileId) {
        Objects.requireNonNull(id, "themeId");
        Objects.requireNonNull(actorProfileId, "actorProfileId");

        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new NichtGefundenException("Theme nicht gefunden: " + id));

        if (!theme.getOwnerProfileId().equals(actorProfileId)) {
            throw new ZugriffVerweigertException("Kein Zugriff: Theme gehört nicht zu diesem Profil.");
        }
        return theme;
    }
}
