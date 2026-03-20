package dse.thesis404.infrastructure.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  @Value("${app.security.admin-ids:}")
  private String adminIdsConfig;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);

    Set<Long> adminIds = parseAdminIds();
    Object idAttr = user.getAttribute("id");
    Long githubId = null;
    if (idAttr instanceof Number num) {
      githubId = num.longValue();
    }

    List<SimpleGrantedAuthority> authorities = new ArrayList<>(user.getAuthorities().stream()
        .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
        .toList());

    if (githubId != null && adminIds.contains(githubId)) {
      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    return new DefaultOAuth2User(
        authorities,
        user.getAttributes(),
        userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
    );
  }

  private Set<Long> parseAdminIds() {
    if (adminIdsConfig == null || adminIdsConfig.isBlank()) {
      return Set.of();
    }
    return Stream.of(adminIdsConfig.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(s -> {
          try {
            return Long.parseLong(s);
          } catch (NumberFormatException e) {
            return null;
          }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
  }
}
