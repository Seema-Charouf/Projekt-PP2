package dse.thesis404.infrastructure.config.security;

import dse.thesis404.application.service.BetreuerProfilService;
import dse.thesis404.testutil.BetreuerProfilMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("RollenZuordnung – welche Rollen bekommt ein GitHub-Nutzer?")
class RollenZuordnungTest {

    private final BetreuerProfilService betreuerProfilService = mock(BetreuerProfilService.class);

    @Test
    void normaler_nutzer_bekommt_keine_zusaetzliche_rolle() {
        when(betreuerProfilService.findByGithubId(42L)).thenReturn(Optional.empty());
        RollenZuordnung zuordnung = new RollenZuordnung(betreuerProfilService, "1,2");

        assertThat(zuordnung.zusaetzlicheRollen(42L)).isEmpty();
    }

    @Test
    void admin_id_bekommt_role_admin() {
        when(betreuerProfilService.findByGithubId(1L)).thenReturn(Optional.empty());
        RollenZuordnung zuordnung = new RollenZuordnung(betreuerProfilService, " 1 , 2 ");

        assertThat(zuordnung.zusaetzlicheRollen(1L))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void nutzer_mit_betreuerprofil_bekommt_role_betreuer() {
        when(betreuerProfilService.findByGithubId(3000L))
                .thenReturn(Optional.of(BetreuerProfilMother.withGithubId(3000)));
        RollenZuordnung zuordnung = new RollenZuordnung(betreuerProfilService, "");

        assertThat(zuordnung.zusaetzlicheRollen(3000L))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_BETREUER");
    }

    @Test
    void admin_mit_betreuerprofil_bekommt_beide_rollen() {
        when(betreuerProfilService.findByGithubId(3000L))
                .thenReturn(Optional.of(BetreuerProfilMother.withGithubId(3000)));
        RollenZuordnung zuordnung = new RollenZuordnung(betreuerProfilService, "3000");

        assertThat(zuordnung.zusaetzlicheRollen(3000L))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_BETREUER");
    }

    @Test
    void ungueltige_admin_ids_werden_ignoriert() {
        when(betreuerProfilService.findByGithubId(5L)).thenReturn(Optional.empty());
        RollenZuordnung zuordnung = new RollenZuordnung(betreuerProfilService, "abc,,5");

        assertThat(zuordnung.zusaetzlicheRollen(5L))
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void ohne_github_id_keine_rollen() {
        RollenZuordnung zuordnung = new RollenZuordnung(betreuerProfilService, "1");

        assertThat(zuordnung.zusaetzlicheRollen(null)).isEmpty();
    }
}
