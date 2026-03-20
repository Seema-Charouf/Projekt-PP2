package dse.thesis404.infrastructure.web.dto;

import dse.thesis404.domain.model.betreuer.BetreuerProfil;

public record ScoredBetreuer(BetreuerProfil betreuer, int score) {}
