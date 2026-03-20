package dse.thesis404.domain.model.student;

import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.ThemeId;

import java.util.Objects;

public record StudentThemeRequest(
        ProfileId studentId,
        ThemeId themeId,
        RequestStatus status
) {
    public StudentThemeRequest {
        Objects.requireNonNull(studentId, "studentId");
        Objects.requireNonNull(themeId, "themeId");
        Objects.requireNonNull(status, "status");
    }
}
