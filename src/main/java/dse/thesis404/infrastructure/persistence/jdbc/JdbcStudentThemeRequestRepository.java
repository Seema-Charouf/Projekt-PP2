package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.application.port.StudentThemeRequestRepository;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.domain.model.student.StudentThemeRequest;
import dse.thesis404.domain.model.theme.ThemeId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcStudentThemeRequestRepository implements StudentThemeRequestRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcStudentThemeRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<StudentThemeRequest> findRequestsForThemes(List<ThemeId> themeIds) {
        if (themeIds == null || themeIds.isEmpty()) {
            return List.of();
        }
        List<UUID> uuids = themeIds.stream().map(ThemeId::value).toList();
        String placeholders = String.join(",", uuids.stream().map(u -> "?").toList());
        String sql = "SELECT profile_id, theme_id, status FROM student_theme_request WHERE theme_id IN (" + placeholders + ")";
        List<Object> args = new ArrayList<>(uuids);
        return jdbcTemplate.query(sql, (rs, rowNum) -> toRequest(rs), args.toArray());
    }

    @Override
    public Optional<StudentThemeRequest> findRequest(ProfileId studentId, ThemeId themeId) {
        String sql = "SELECT profile_id, theme_id, status FROM student_theme_request WHERE profile_id = ? AND theme_id = ?";
        List<StudentThemeRequest> list = jdbcTemplate.query(sql, (rs, rowNum) -> toRequest(rs),
                studentId.value(), themeId.value());
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public void save(StudentThemeRequest request) {
        String sql = """
                INSERT INTO student_theme_request (profile_id, theme_id, status, requested_at)
                VALUES (?, ?, ?, NOW())
                ON CONFLICT (profile_id, theme_id) DO UPDATE SET status = EXCLUDED.status
                """;
        jdbcTemplate.update(sql, request.studentId().value(), request.themeId().value(), request.status().name());
    }

    @Override
    public boolean exists(ProfileId studentId, ThemeId themeId) {
        String sql = "SELECT 1 FROM student_theme_request WHERE profile_id = ? AND theme_id = ?";
        Boolean found = jdbcTemplate.query(sql, rs -> rs.next() ? true : null, studentId.value(), themeId.value());
        return Boolean.TRUE.equals(found);
    }

    private StudentThemeRequest toRequest(java.sql.ResultSet rs) throws java.sql.SQLException {
        long profileId = rs.getLong("profile_id");
        UUID themeId = (UUID) rs.getObject("theme_id");
        String statusStr = rs.getString("status");
        RequestStatus status = RequestStatus.valueOf(statusStr != null ? statusStr : "PENDING");
        return new StudentThemeRequest(new ProfileId(profileId), new ThemeId(themeId), status);
    }
}
