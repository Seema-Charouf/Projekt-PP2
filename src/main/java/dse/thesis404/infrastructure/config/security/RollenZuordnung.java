package dse.thesis404.infrastructure.config.security;

import dse.thesis404.application.service.BetreuerProfilService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Entscheidet, welche zusätzlichen Rollen ein GitHub-Nutzer beim Login bekommt.
 * Bewusst als eigene Klasse ohne OAuth-Abhängigkeit, damit die Logik per Unit-Test prüfbar ist.
 */
@Component
public class RollenZuordnung {

  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_BETREUER = "ROLE_BETREUER";

  private final BetreuerProfilService betreuerProfilService;
  private final Set<Long> adminIds;

  public RollenZuordnung(
      BetreuerProfilService betreuerProfilService,
      @Value("${app.security.admin-ids:}") String adminIdsConfig
  ) {
    this.betreuerProfilService = Objects.requireNonNull(betreuerProfilService, "betreuerProfilService");
    this.adminIds = parseAdminIds(adminIdsConfig);
  }

  public Set<GrantedAuthority> zusaetzlicheRollen(Long githubId) {
    Set<GrantedAuthority> rollen = new LinkedHashSet<>();
    if (githubId == null) {
      return rollen;
    }
    if (adminIds.contains(githubId)) {
      rollen.add(new SimpleGrantedAuthority(ROLE_ADMIN));
    }
    if (betreuerProfilService.findByGithubId(githubId).isPresent()) {
      rollen.add(new SimpleGrantedAuthority(ROLE_BETREUER));
    }
    return rollen;
  }

  private static Set<Long> parseAdminIds(String config) {
    if (config == null || config.isBlank()) {
      return Set.of();
    }
    return Stream.of(config.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(RollenZuordnung::parseOrNull)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
  }

  private static Long parseOrNull(String s) {
    try {
      return Long.parseLong(s);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}
