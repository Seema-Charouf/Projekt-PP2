package dse.thesis404.application.dto;

import dse.thesis404.domain.model.betreuer.BetreuerProfil;

public record ScoredBetreuer(BetreuerProfil betreuer, int score) {}
