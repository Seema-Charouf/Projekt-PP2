package dse.thesis404.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class OAuth2CredentialsValidator {

  private static final Logger log = LoggerFactory.getLogger(OAuth2CredentialsValidator.class);

  private static final String HELP_MSG = """
      Setze die Umgebungsvariablen CLIENT_ID und CLIENT_SECRET.
      Siehe .env.example und README für Anleitung.
      """;

  @Value("${spring.security.oauth2.client.registration.github.client-id:}")
  private String clientId;

  @Value("${spring.security.oauth2.client.registration.github.client-secret:}")
  private String clientSecret;

  private final Environment environment;

  public OAuth2CredentialsValidator(Environment environment) {
    this.environment = environment;
  }

  @PostConstruct
  public void validate() {
    if (isInvalid(clientId) || isInvalid(clientSecret)) {
      boolean isProd = java.util.Arrays.asList(environment.getActiveProfiles()).contains("prod");
      if (isProd) {
        log.error("GitHub OAuth2 Credentials fehlen. {}", HELP_MSG);
        throw new IllegalStateException(
            "CLIENT_ID und CLIENT_SECRET müssen gesetzt sein. Siehe README.");
      } else {
        log.warn("GitHub OAuth2 Credentials nicht gesetzt – Login wird fehlschlagen. {}", HELP_MSG);
      }
    }
  }

  private static boolean isInvalid(String value) {
    return value == null || value.isBlank() || value.startsWith("__MISSING");
  }
}
