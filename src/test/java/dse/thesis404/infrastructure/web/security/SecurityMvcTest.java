package dse.thesis404.infrastructure.web.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security/MVC – Zugriffskontrolle")
class SecurityMvcTest {

    @Autowired
    private MockMvc mvc;

    @Nested
    @DisplayName("Public Endpunkte: permitAll → 200")
    class PublicEndpoints {

        @Test
        void login_ist_oeffentlich() throws Exception {
            mvc.perform(get("/login"))
                    .andExpect(status().isOk());
        }

        @Test
        void root_liefert_login() throws Exception {
            mvc.perform(get("/"))
                    .andExpect(status().isOk());
        }

        @Test
        void homestudi_ist_oeffentlich() throws Exception {
            mvc.perform(get("/homestudi"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Protected Endpunkte: ohne Login → Redirect")
    class ProtectedEndpoints {

        @Test
        void admin_ohne_login_redirectet_zum_login() throws Exception {
            mvc.perform(get("/admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("**/login**"));
        }

        @Test
        void addProfile_ohne_login_redirectet() throws Exception {
            mvc.perform(get("/addProfile"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        void addTopics_ohne_login_redirectet() throws Exception {
            mvc.perform(get("/addTopics"))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        void betreuerProfil_ohne_login_redirectet() throws Exception {
            mvc.perform(get("/betreuerProfil"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("Admin-Bereich: nur ROLE_ADMIN")
    class AdminAccess {

        @Test
        void admin_mit_normalem_user_403() throws Exception {
            mvc.perform(get("/admin").with(user("test").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void addProfile_mit_normalem_user_403() throws Exception {
            mvc.perform(get("/addProfile").with(user("test").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void addusers_mit_normalem_user_403() throws Exception {
            mvc.perform(get("/addusers").with(user("test").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void admin_mit_github_login_ohne_adminrolle_403() throws Exception {
            mvc.perform(get("/admin").with(oauth2Login()
                            .attributes(a -> a.put("id", 12345L))
                            .authorities(new SimpleGrantedAuthority("ROLE_USER"))))
                    .andExpect(status().isForbidden());
        }

        @Test
        void admin_mit_adminrolle_200() throws Exception {
            mvc.perform(get("/admin").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk());
        }

        @Test
        void addProfile_mit_adminrolle_200() throws Exception {
            mvc.perform(get("/addProfile").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isOk());
        }

        @Test
        void admin_mit_github_login_und_adminrolle_200() throws Exception {
            mvc.perform(get("/admin").with(oauth2Login()
                            .attributes(a -> a.put("id", 99999L))
                            .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Betreuer-Bereich: nur ROLE_BETREUER")
    class BetreuerAccess {

        @Test
        void betreuerProfil_mit_normalem_user_403() throws Exception {
            mvc.perform(get("/betreuerProfil").with(user("test").roles("USER")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void betreuerProfil_mit_adminrolle_ohne_betreuerrolle_403() throws Exception {
            mvc.perform(get("/betreuerProfil").with(user("admin").roles("ADMIN")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void betreuerProfil_mit_betreuerrolle_wird_nicht_von_security_blockiert() throws Exception {
            mvc.perform(get("/betreuerProfil").with(user("betreuer").roles("BETREUER")))
                    .andExpect(result -> assertThat(result.getResponse().getStatus()).isNotEqualTo(403));
        }
    }

    @Nested
    @DisplayName("Form-POSTs: CSRF")
    class Csrf {

        @Test
        void post_addProfile_ohne_csrf_403() throws Exception {
            mvc.perform(post("/addProfile")
                            .param("name", "Test")
                            .param("fachgebiete", "AI")
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().isForbidden());
        }

        @Test
        void post_addProfile_mit_csrf_ok() throws Exception {
            mvc.perform(post("/addProfile")
                            .with(csrf())
                            .param("name", "Test")
                            .param("fachgebiete", "AI")
                            .with(user("admin").roles("ADMIN")))
                    .andExpect(status().is3xxRedirection());
        }

        @Test
        void post_addProfile_als_normaler_user_403() throws Exception {
            mvc.perform(post("/addProfile")
                            .with(csrf())
                            .param("name", "Test")
                            .param("fachgebiete", "AI")
                            .with(user("test").roles("USER")))
                    .andExpect(status().isForbidden());
        }
    }
}
