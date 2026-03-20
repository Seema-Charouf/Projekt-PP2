package dse.thesis404.application.dto;

import dse.thesis404.domain.model.theme.Theme;

public record ThemeWithOwner(Theme theme, String ownerName) {}
