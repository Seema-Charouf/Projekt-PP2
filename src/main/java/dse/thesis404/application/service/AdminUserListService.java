package dse.thesis404.application.service;

import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.application.port.StudentRepository;
import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.Student;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.Veranstaltung;
import dse.thesis404.infrastructure.web.dto.AdminUserRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AdminUserListService {

    private final BetreuerProfilRepository betreuerProfilRepository;
    private final ThemeRepository themeRepository;
    private final StudentRepository studentRepository;

    @Value("${app.security.admin-ids:}")
    private String adminIdsConfig;

    public AdminUserListService(
            BetreuerProfilRepository betreuerProfilRepository,
            ThemeRepository themeRepository,
            StudentRepository studentRepository
    ) {
        this.betreuerProfilRepository = betreuerProfilRepository;
        this.themeRepository = themeRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserRow> getAdminUserRows() {
        Set<Long> adminIds = parseAdminIds();
        List<AdminUserRow> rows = new ArrayList<>();

        for (BetreuerProfil b : betreuerProfilRepository.findAll()) {
            long id = b.getId().value();
            String role = buildRole(id, adminIds, true, false);
            String modules = getModulesForBetreuer(b.getId());
            rows.add(new AdminUserRow(id, b.getFullName(), role, String.valueOf(id), modules));
        }

        for (Student s : studentRepository.findAll()) {
            long id = s.getId().value();
            if (rows.stream().anyMatch(r -> r.id() == id)) continue;
            String name = s.getDisplayName() != null && !s.getDisplayName().isBlank()
                    ? s.getDisplayName() : "Student " + id;
            String role = buildRole(id, adminIds, false, true);
            String modules = String.join(", ", s.getBesuchteVeranstaltungen());
            rows.add(new AdminUserRow(id, name, role, String.valueOf(id), modules));
        }

        for (Long adminId : adminIds) {
            if (rows.stream().anyMatch(r -> r.id() == adminId)) continue;
            rows.add(new AdminUserRow(adminId, "Admin (ID: " + adminId + ")", "Admin", String.valueOf(adminId), "—"));
        }

        rows.sort((a, b) -> Long.compare(a.id(), b.id()));
        return rows;
    }

    private String buildRole(long id, Set<Long> adminIds, boolean isBetreuer, boolean isStudent) {
        List<String> roles = new ArrayList<>();
        if (adminIds.contains(id)) roles.add("Admin");
        if (isBetreuer) roles.add("Betreuer");
        if (isStudent) roles.add("Student");
        return roles.isEmpty() ? "—" : String.join(", ", roles);
    }

    private String getModulesForBetreuer(ProfileId profileId) {
        List<Theme> themes = themeRepository.findByOwnerProfileId(profileId);
        Set<String> names = new LinkedHashSet<>();
        for (Theme t : themes) {
            for (Veranstaltung v : t.getAngeforderteVeranstaltungen()) {
                names.add(v.name());
            }
        }
        return names.isEmpty() ? "—" : String.join(", ", names);
    }

    private Set<Long> parseAdminIds() {
        if (adminIdsConfig == null || adminIdsConfig.isBlank()) return Set.of();
        return Stream.of(adminIdsConfig.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try { return Long.parseLong(s); } catch (NumberFormatException e) { return null; }
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
