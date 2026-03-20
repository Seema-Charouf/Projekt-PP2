package dse.thesis404.application.service;

import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.student.RequestStatus;
import dse.thesis404.domain.model.theme.ThemeId;

public record StudentThemeRequestView(
        ProfileId studentId,
        ThemeId themeId,
        String themeTitle,
        String studentDisplayName,
        RequestStatus status
) {}
