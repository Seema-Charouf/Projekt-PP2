package dse.thesis404.application.port;

import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.domain.model.student.StudentThemeRequest;
import dse.thesis404.domain.model.theme.ThemeId;

import java.util.List;
import java.util.Optional;

public interface StudentThemeRequestRepository {

    List<StudentThemeRequest> findRequestsForThemes(List<ThemeId> themeIds);

    Optional<StudentThemeRequest> findRequest(ProfileId studentId, ThemeId themeId);

    void save(StudentThemeRequest request);

    boolean exists(ProfileId studentId, ThemeId themeId);
}
