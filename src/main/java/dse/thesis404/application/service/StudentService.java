package dse.thesis404.application.service;

import dse.thesis404.application.dto.ThemeWithOwner;
import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.application.port.StudentThemeRequestRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.shared.Fachgebiet;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.domain.model.student.Student;
import dse.thesis404.domain.model.student.StudentThemeRequest;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.domain.model.theme.Veranstaltung;
import dse.thesis404.application.dto.ScoredBetreuer;
import dse.thesis404.application.dto.ScoredTheme;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final BetreuerProfilRepository betreuerProfilRepository;
    private final ThemeRepository themeRepository;
    private final StudentRepository studentRepository;
    private final StudentThemeRequestRepository studentThemeRequestRepository;

    
    private static final int SCORE_VERANSTALTUNG = 2;
    
    private static final int SCORE_INTERESSE = 1;

    public StudentService(
            BetreuerProfilRepository betreuerProfilRepository,
            ThemeRepository themeRepository,
            StudentRepository studentRepository,
            StudentThemeRequestRepository studentThemeRequestRepository
    ) {
        this.betreuerProfilRepository = Objects.requireNonNull(betreuerProfilRepository);
        this.themeRepository = Objects.requireNonNull(themeRepository);
        this.studentRepository = Objects.requireNonNull(studentRepository);
        this.studentThemeRequestRepository = Objects.requireNonNull(studentThemeRequestRepository);
    }

    
    @Transactional
    public Student getOrCreateStudent(ProfileId id, String displayName) {
        return studentRepository.findById(id)
                .orElseGet(() -> {
                    Student s = new Student(id);
                    s.setDisplayName(displayName);
                    studentRepository.save(s);
                    return s;
                });
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudent(ProfileId id) {
        return studentRepository.findById(id);
    }

    
    @Transactional
    public void saveStudentPreferences(ProfileId studentId, Set<String> attended, Set<String> interessen) {
        Student s = getOrCreateStudent(studentId, null);
        s.setBesuchteVeranstaltungen(attended != null ? attended : Set.of());
        s.setInteressen(interessen != null ? interessen : Set.of());
        studentRepository.save(s);
    }

    
    @Transactional
    public void requestTheme(ProfileId studentId, ThemeId themeId) {
        if (studentThemeRequestRepository.exists(studentId, themeId)) {
            return;
        }
        studentThemeRequestRepository.save(new StudentThemeRequest(studentId, themeId, RequestStatus.PENDING));
    }

    @Transactional(readOnly = true)
    public boolean hasRequestedTheme(ProfileId studentId, ThemeId themeId) {
        return studentThemeRequestRepository.exists(studentId, themeId);
    }

    
    @Transactional(readOnly = true)
    public List<ScoredTheme> findMatchingThemes(Set<String> attended, Set<String> interessen) {
        Set<String> a = normalize(attended);
        Set<String> i = normalize(interessen);
        if (a.isEmpty() && i.isEmpty()) {
            return List.of();
        }

        List<ScoredTheme> result = new ArrayList<>();
        for (Theme t : themeRepository.findAll()) {
            int score = themeScore(t, a, i);
            if (score <= 0) continue;
            String ownerName = betreuerProfilRepository.findById(t.getOwnerProfileId())
                    .map(BetreuerProfil::getFullName)
                    .orElse("?");
            result.add(new ScoredTheme(t, score, ownerName));
        }
        result.sort(Comparator.comparingInt(ScoredTheme::score).reversed());
        return result;
    }

    
    @Transactional(readOnly = true)
    public List<ScoredBetreuer> findMatchingBetreuer(Set<String> attended, Set<String> interessen) {
        Set<String> a = normalize(attended);
        Set<String> i = normalize(interessen);
        if (a.isEmpty() && i.isEmpty()) {
            return List.of();
        }

        List<Theme> allThemes = themeRepository.findAll();
        Map<ProfileId, Integer> themeScoresByOwner = new HashMap<>();

        for (Theme t : allThemes) {
            int score = themeScore(t, a, i);
            if (score <= 0) continue;
            themeScoresByOwner.merge(t.getOwnerProfileId(), score, Integer::sum);
        }

        List<ScoredBetreuer> result = new ArrayList<>();
        for (BetreuerProfil b : betreuerProfilRepository.findAll()) {
            int score = themeScoresByOwner.getOrDefault(b.getId(), 0);
            for (Fachgebiet fg : b.getFachgebiete()) {
                if (i.contains(fg.key())) {
                    score += SCORE_INTERESSE;
                }
            }
            if (score <= 0) continue;
            result.add(new ScoredBetreuer(b, score));
        }
        result.sort(Comparator.comparingInt(ScoredBetreuer::score).reversed());
        return result;
    }

    private int themeScore(Theme t, Set<String> attended, Set<String> interessen) {
        int score = 0;
        for (Veranstaltung v : t.getAngeforderteVeranstaltungen()) {
            if (attended.contains(v.key())) {
                score += SCORE_VERANSTALTUNG;
            }
        }
        for (Fachgebiet fg : t.getFachgebiete()) {
            if (interessen.contains(fg.key())) {
                score += SCORE_INTERESSE;
            }
        }
        return score;
    }

    private static Set<String> normalize(Set<String> raw) {
        if (raw == null || raw.isEmpty()) return Set.of();
        return raw.stream()
                .filter(Objects::nonNull)
                .map(s -> s.trim().toLowerCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Optional<BetreuerProfil> getBetreuerById(ProfileId id) {
        return betreuerProfilRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    
    @Transactional(readOnly = true)
    public List<ThemeWithOwner> getThemesWithOwnerNames() {
        List<Theme> themes = themeRepository.findAll();
        List<ThemeWithOwner> result = new ArrayList<>();
        for (Theme t : themes) {
            String ownerName = betreuerProfilRepository.findById(t.getOwnerProfileId())
                    .map(BetreuerProfil::getFullName)
                    .orElse("?");
            result.add(new ThemeWithOwner(t, ownerName));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<Theme> getThemesByBetreuer(ProfileId profileId) {
        return themeRepository.findByOwnerProfileId(profileId);
    }

    @Transactional(readOnly = true)
    public Set<String> getModuleNamesForBetreuer(ProfileId profileId) {
        List<Theme> themes = themeRepository.findByOwnerProfileId(profileId);
        Set<String> names = new LinkedHashSet<>();
        for (Theme t : themes) {
            for (Veranstaltung v : t.getAngeforderteVeranstaltungen()) {
                names.add(v.name());
            }
        }
        return names;
    }
}
