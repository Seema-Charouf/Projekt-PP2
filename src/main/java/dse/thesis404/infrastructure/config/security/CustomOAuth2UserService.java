package dse.thesis404.infrastructure.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final RollenZuordnung rollenZuordnung;

  public CustomOAuth2UserService(RollenZuordnung rollenZuordnung) {
    this.rollenZuordnung = rollenZuordnung;
  }

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);

    Object idAttr = user.getAttribute("id");
    Long githubId = (idAttr instanceof Number num) ? num.longValue() : null;

    Set<GrantedAuthority> authorities = new LinkedHashSet<>(user.getAuthorities());
    authorities.addAll(rollenZuordnung.zusaetzlicheRollen(githubId));

    return new DefaultOAuth2User(
        authorities,
        user.getAttributes(),
        userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
    );
  }
}
