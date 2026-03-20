package dse.thesis404.infrastructure.config.security;

import dse.thesis404.application.service.BetreuerProfilService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final BetreuerProfilService betreuerProfilService;

  public OAuth2AuthenticationSuccessHandler(BetreuerProfilService betreuerProfilService) {
    this.betreuerProfilService = betreuerProfilService;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) throws IOException, ServletException {
    Collection<String> authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());

    if (authorities.contains("ROLE_ADMIN")) {
      response.sendRedirect(request.getContextPath() + "/homeadmin");
      return;
    }

    Long githubId = OAuth2PrincipalUtils.extractGitHubId(authentication).orElse(null);
    if (githubId != null && betreuerProfilService.findByGithubId(githubId).isPresent()) {
      response.sendRedirect(request.getContextPath() + "/betreuerProfil");
      return;
    }

    response.sendRedirect(request.getContextPath() + "/homestudi");
  }
}
