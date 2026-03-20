package dse.thesis404.infrastructure.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2AuthenticationSuccessHandler authenticationSuccessHandler;

  public SecurityConfig(
      CustomOAuth2UserService customOAuth2UserService,
      OAuth2AuthenticationSuccessHandler authenticationSuccessHandler
  ) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.authenticationSuccessHandler = authenticationSuccessHandler;
  }

  @Bean
  public SecurityFilterChain configure(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/public/**", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
            .requestMatchers("/homestudi", "/homestudi/**").permitAll()
            .anyRequest().authenticated())
        .oauth2Login(oauth -> oauth
            .loginPage("/login")
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            .successHandler(authenticationSuccessHandler))
        .logout(logout -> logout
            .logoutSuccessUrl("/")
            .permitAll());
    return http.build();
  }

}
