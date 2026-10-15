package dse.thesis404.application.service;

import dse.thesis404.application.exception.ZugriffVerweigertException;

import dse.thesis404.application.exception.NichtGefundenException;
import dse.thesis404.application.command.UploadInfoFileCommand;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.FileStoragePort;
import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.application.port.StudentThemeRequestRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.betreuer.Titel;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.FileId;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.domain.model.student.StudentThemeRequest;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BetreuerProfilService {

    private final BetreuerProfilRepository profilRepository;
    private final DateiUploadService dateiUploadService;
    private final ThemeRepository themeRepository;
    private final StudentThemeRequestRepository studentThemeRequestRepository;
    private final StudentRepository studentRepository;

    public BetreuerProfilService(
            BetreuerProfilRepository profilRepository,
            DateiUploadService dateiUploadService,
            ThemeRepository themeRepository,
            StudentThemeRequestRepository studentThemeRequestRepository,
            StudentRepository studentRepository
    ) {
        this.profilRepository = Objects.requireNonNull(profilRepository, "profilRepository");
        this.dateiUploadService = Objects.requireNonNull(dateiUploadService, "dateiUploadService");
        this.themeRepository = Objects.requireNonNull(themeRepository, "themeRepository");
        this.studentThemeRequestRepository = Objects.requireNonNull(studentThemeRequestRepository, "studentThemeRequestRepository");
        this.studentRepository = Objects.requireNonNull(studentRepository, "studentRepository");
    }

    
    @Transactional(readOnly = true)
    public List<StudentThemeRequestView> getRequestsForBetreuer(ProfileId betreuerId) {
        List<Theme> myThemes = themeRepository.findByOwnerProfileId(betreuerId);
        if (myThemes.isEmpty()) {
            return List.of();
        }
        List<ThemeId> themeIds = myThemes.stream().map(Theme::getId).toList();
        List<StudentThemeRequest> requests = studentThemeRequestRepository.findRequestsForThemes(themeIds);
        List<StudentThemeRequestView> result = new ArrayList<>();
        for (StudentThemeRequest req : requests) {
            String themeTitle = myThemes.stream()
                    .filter(t -> t.getId().equals(req.themeId()))
                    .map(Theme::getTitel)
                    .findFirst()
                    .orElse("?");
            String studentName = studentRepository.findById(req.studentId())
                    .map(s -> s.getDisplayName() != null ? s.getDisplayName() : "gh:" + req.studentId().value())
                    .orElse("gh:" + req.studentId().value());
            result.add(new StudentThemeRequestView(req.studentId(), req.themeId(), themeTitle, studentName, req.status()));
        }
        return result;
    }

    
    @Transactional
    public void acceptRequest(ProfileId betreuerId, ProfileId studentId, ThemeId themeId) {
        pruefeEigeneAnfrage(betreuerId, studentId, themeId);
        studentThemeRequestRepository.save(new StudentThemeRequest(studentId, themeId, RequestStatus.ACCEPTED));
    }

    
    @Transactional
    public void declineRequest(ProfileId betreuerId, ProfileId studentId, ThemeId themeId) {
        pruefeEigeneAnfrage(betreuerId, studentId, themeId);
        studentThemeRequestRepository.save(new StudentThemeRequest(studentId, themeId, RequestStatus.DECLINED));
    }

    /**
     * Ein Betreuer darf nur Anfragen zu seinen eigenen Themen bearbeiten – und nur Anfragen, die es wirklich gibt.
     * Vorher konnte man durch "Annehmen" einer erfundenen Anfrage-ID einen neuen Datensatz erzeugen.
     */
    private void pruefeEigeneAnfrage(ProfileId betreuerId, ProfileId studentId, ThemeId themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new NichtGefundenException("Thema nicht gefunden: " + themeId));
        if (!theme.getOwnerProfileId().equals(betreuerId)) {
            throw new ZugriffVerweigertException("Thema " + themeId + " gehört nicht Betreuer " + betreuerId);
        }
        if (!studentThemeRequestRepository.exists(studentId, themeId)) {
            throw new NichtGefundenException("Keine Anfrage von " + studentId + " zu Thema " + themeId);
        }
    }

    /** Liefert eine Datei nur, wenn sie zum Profil des anfragenden Betreuers gehört. */
    @Transactional(readOnly = true)
    public Datei getEigeneDatei(ProfileId profileId, FileId fileId) {
        Objects.requireNonNull(fileId, "fileId");
        return getById(profileId).getFiles().stream()
                .filter(f -> f.id().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new NichtGefundenException("Datei nicht gefunden: " + fileId));
    }

    @Transactional(readOnly = true)
    public BetreuerProfil getById(ProfileId id) {
        return profilRepository.findById(id)
                .orElseThrow(() -> new NichtGefundenException("BetreuerProfil nicht gefunden: " + id));
    }

    @Transactional(readOnly = true)
    public Optional<BetreuerProfil> findByGithubId(long githubId) {
        return profilRepository.findByGithubId(githubId);
    }

    @Transactional
    public void updateTitel(ProfileId id, Titel titel) {
        BetreuerProfil profil = getById(id);
        profil.updateTitel(titel);
        profilRepository.save(profil);
    }

    @Transactional
    public void updateName(ProfileId id, String name) {
        BetreuerProfil profil = getById(id);
        profil.updateName(name);
        profilRepository.save(profil);
    }

    @Transactional
    public void updateEmail(ProfileId id, String email) {
        BetreuerProfil profil = getById(id);
        profil.updateEmail(email);
        profilRepository.save(profil);
    }

    @Transactional
    public void updatePhone(ProfileId id, String phone) {
        BetreuerProfil profil = getById(id);
        profil.updatePhoneNumber(phone);
        profilRepository.save(profil);
    }

    @Transactional
    public void updateRaum(ProfileId id, String raum) {
        BetreuerProfil profil = getById(id);
        profil.updateRaum(raum);
        profilRepository.save(profil);
    }

    @Transactional
    public void addFachgebiet(ProfileId id, String fachgebietName) {
        BetreuerProfil profil = getById(id);
        profil.addFachgebiet(fachgebietName);
        profilRepository.save(profil);
    }

    @Transactional
    public void deleteFachgebietByKey(ProfileId id, String key) {
        BetreuerProfil profil = getById(id);
        profil.deleteFachgebietByKey(key);
        profilRepository.save(profil);
    }

    @Transactional
    public void addLink(ProfileId id, String url, String text) {
        BetreuerProfil profil = getById(id);
        profil.addLink(url, text);
        profilRepository.save(profil);
    }

    @Transactional
    public void deleteLink(ProfileId id, String url, String text) {
        BetreuerProfil profil = getById(id);
        profil.deleteLink(url, text);
        profilRepository.save(profil);
    }

    @Transactional
    public FileId uploadInfoFile(ProfileId profileId, String uploaderId, UploadInfoFileCommand cmd) {
        Objects.requireNonNull(profileId, "profileId");
        Objects.requireNonNull(uploaderId, "uploaderId");
        Objects.requireNonNull(cmd, "cmd");

        BetreuerProfil profil = getById(profileId);

        var result = dateiUploadService.storeAndCreateMeta(uploaderId, cmd);
        Datei meta = result.meta();

        try {
            profil.addFile(meta);
            profilRepository.save(profil);
        } catch (RuntimeException ex) {

            try {
                dateiUploadService.deleteFromStorage(meta);
            } catch (Exception ignore) {

            }
            throw ex;
        }

        return meta.id();
    }

    @Transactional
    public void deleteFile(ProfileId profileId, FileId fileId) {
        Objects.requireNonNull(profileId, "profileId");
        Objects.requireNonNull(fileId, "fileId");

        BetreuerProfil profil = getById(profileId);

        Datei file = profil.getFiles().stream()
                .filter(f -> f.id().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new NichtGefundenException("Datei nicht gefunden: " + fileId));

        profil.deleteFileById(fileId);
        profilRepository.save(profil);

        boolean deleted = dateiUploadService.deleteFromStorage(file);
        if (!deleted) {
            throw new FileStoragePort.FileStorageException(
                    "Datei konnte nicht aus dem Dateisystem gelöscht werden: " + file.storedFilename()
            );
        }
    }
}
