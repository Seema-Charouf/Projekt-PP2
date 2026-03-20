package dse.thesis404.infrastructure.web.dto;

import dse.thesis404.domain.model.theme.Theme;

public record ScoredTheme(Theme theme, int score, String betreuerName) {}
