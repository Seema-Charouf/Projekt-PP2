package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.application.service.StudentService;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.Student;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.infrastructure.config.security.OAuth2PrincipalUtils;
import dse.thesis404.application.dto.ThemeWithOwner;
import dse.thesis404.application.dto.ScoredBetreuer;
import dse.thesis404.application.dto.ScoredTheme;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/homestudi")
public class StudentController {

    
    private static final List<String> VERANSTALTUNGEN_OPTIONS = List.of(
            "Programmierung",
            "Algorithmen und Datenstrukturen",
            "Theoretische Informatik",
            "Softwaretechnik",
            "Datenbanksysteme",
            "Rechnernetze",
            "Betriebssysteme",
            "Propra 1",
            "Propra 2",
            "Lineare Algebra",
            "Analysis",
            "Datenbanken",
            "Machine Learning",
            "Data Science",
            "IT-Sicherheit",
            "Verteilte Systeme",
            "Big Data"
    );

    
    private static final List<String> INTERESSEN_OPTIONS = List.of(
            "Software Engineering",
            "Künstliche Intelligenz",
            "Datenbanken",
            "IT-Sicherheit",
            "Verteilte Systeme",
            "Deep Learning",
            "Machine Learning",
            "Big Data",
            "NoSQL",
            "Cloud Computing",
            "Microservices",
            "Backend-Entwicklung",
            "Qualitätssicherung",
            "Web Security",
            "Natural Language Processing",
            "Agile Methoden",
            "Clean Code"
    );

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = Objects.requireNonNull(studentService);
    }

    
    @GetMapping
    public String studi(
            @RequestParam(required = false) List<String> attended,
            @RequestParam(required = false) List<String> interesse,
            @RequestParam(required = false) Long selectedId,
            Principal principal,
            Model model
    ) {
        model.addAttribute("veranstaltungenOptions", VERANSTALTUNGEN_OPTIONS);
        model.addAttribute("interessenOptions", INTERESSEN_OPTIONS);

        Set<String> attendedSet = new LinkedHashSet<>(toSet(attended));
        Set<String> interessenSet = new LinkedHashSet<>(toSet(interesse));

        if (principal != null) {
            OAuth2PrincipalUtils.extractProfileId(principal).ifPresent(profileId -> {
                String displayName = OAuth2PrincipalUtils.displayName(principal);
                Student student = studentService.getOrCreateStudent(profileId, displayName);
                if (attendedSet.isEmpty() && interessenSet.isEmpty()) {
                    attendedSet.addAll(student.getBesuchteVeranstaltungen());
                    interessenSet.addAll(student.getInteressen());
                } else {
                    studentService.saveStudentPreferences(profileId, attendedSet, interessenSet);
                }
            });
        }

        model.addAttribute("selectedAttended", attendedSet);
        model.addAttribute("selectedInteressen", interessenSet);

        List<ScoredTheme> matchingThemen = List.of();
        List<ScoredBetreuer> matchingBetreuer = List.of();
        if (!attendedSet.isEmpty() || !interessenSet.isEmpty()) {
            matchingThemen = studentService.findMatchingThemes(attendedSet, interessenSet);
            matchingBetreuer = studentService.findMatchingBetreuer(attendedSet, interessenSet);
        }
        model.addAttribute("matchingThemen", matchingThemen);
        model.addAttribute("matchingBetreuer", matchingBetreuer);

        Map<Long, Integer> betreuerThemeCounts = new HashMap<>();
        Map<Long, List<String>> betreuerThemeSamples = new HashMap<>();
        for (ScoredBetreuer sb : matchingBetreuer) {
            List<Theme> themes = studentService.getThemesByBetreuer(sb.betreuer().getId());
            betreuerThemeCounts.put(sb.betreuer().getId().value(), themes.size());
            betreuerThemeSamples.put(sb.betreuer().getId().value(),
                    themes.stream().limit(2).map(Theme::getTitel).toList());
        }
        model.addAttribute("betreuerThemeCounts", betreuerThemeCounts);
        model.addAttribute("betreuerThemeSamples", betreuerThemeSamples);

        BetreuerProfil selected = null;
        if (selectedId != null && selectedId > 0) {
            selected = studentService.getBetreuerById(ProfileId.ofGithubUserId(selectedId)).orElse(null);
        }
        model.addAttribute("selectedBetreuer", selected);

        if (selected != null) {
            model.addAttribute("betreuerThemes", studentService.getThemesByBetreuer(selected.getId()));
            model.addAttribute("betreuerModules", studentService.getModuleNamesForBetreuer(selected.getId()));
        } else {
            model.addAttribute("betreuerThemes", List.<Theme>of());
            model.addAttribute("betreuerModules", Set.<String>of());
        }

        boolean isLoggedIn = principal != null && OAuth2PrincipalUtils.extractProfileId(principal).isPresent();
        model.addAttribute("isLoggedIn", isLoggedIn);
        Set<String> requestedThemeIds = Set.of();
        if (isLoggedIn && !matchingThemen.isEmpty()) {
            ProfileId pid = OAuth2PrincipalUtils.extractProfileId(principal).orElseThrow();
            requestedThemeIds = matchingThemen.stream()
                    .filter(st -> studentService.hasRequestedTheme(pid, st.theme().getId()))
                    .map(st -> st.theme().getId().value().toString())
                    .collect(Collectors.toSet());
        }
        model.addAttribute("requestedThemeIds", requestedThemeIds);

        return "studi";
    }

    
    @GetMapping("/themen")
    public String alleThemen(Principal principal, Model model) {
        List<ThemeWithOwner> themesWithOwner = studentService.getThemesWithOwnerNames();
        model.addAttribute("themesWithOwner", themesWithOwner);
        boolean isLoggedIn = principal != null && OAuth2PrincipalUtils.extractProfileId(principal).isPresent();
        model.addAttribute("isLoggedIn", isLoggedIn);
        Set<String> requestedThemeIds = new LinkedHashSet<>();
        if (isLoggedIn && !themesWithOwner.isEmpty()) {
            ProfileId pid = OAuth2PrincipalUtils.extractProfileId(principal).orElseThrow();
            for (ThemeWithOwner two : themesWithOwner) {
                if (studentService.hasRequestedTheme(pid, two.theme().getId())) {
                    requestedThemeIds.add(two.theme().getId().value().toString());
                }
            }
        }
        model.addAttribute("requestedThemeIds", requestedThemeIds);
        return "studiThemen";
    }

    
    @PostMapping("/request/{themeId}")
    public String requestTheme(@PathVariable("themeId") String rawThemeId, Principal principal, RedirectAttributes ra) {
        ThemeId themeId;
        try {
            themeId = ThemeId.parse(rawThemeId);
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", "Ungültige Themen-ID.");
            return "redirect:/homestudi";
        }
        if (principal == null) {
            ra.addFlashAttribute("error", "Bitte einloggen, um ein Thema anzufragen.");
            return "redirect:/homestudi";
        }
        ProfileId studentId = OAuth2PrincipalUtils.extractProfileId(principal)
                .orElse(null);
        if (studentId == null) {
            ra.addFlashAttribute("error", "Benutzer konnte nicht zugeordnet werden.");
            return "redirect:/homestudi";
        }
        studentService.requestTheme(studentId, themeId);
        ra.addFlashAttribute("info", "Anfrage wurde gesendet.");
        return "redirect:/homestudi/themen";
    }


    private static Set<String> toSet(List<String> list) {
        if (list == null) return Set.of();
        return list.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
}
