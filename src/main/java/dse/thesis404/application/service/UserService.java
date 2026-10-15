package dse.thesis404.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

// TODO: Platzhalter aus dem Uni-Projekt – legt noch keinen Nutzer an, sondern protokolliert nur.
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    public void createUser(String githubId, String role) {
        log.info("Nutzer angefordert: githubId={}, rolle={}", githubId, role);
    }
}
