package dse.thesis404.infrastructure.web.admin;

import dse.thesis404.application.service.AddProfileService;
import dse.thesis404.application.service.AdminUserListService;
import dse.thesis404.application.service.TopicService;
import dse.thesis404.application.service.UserService;
import dse.thesis404.infrastructure.web.controller.WebController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(WebController.class)
@WithMockUser
@ActiveProfiles("test")
@DisplayName("WebController – Admin")
class WebControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private TopicService topicService;
    @MockitoBean
    private AddProfileService addProfileService;
    @MockitoBean
    private AdminUserListService adminUserListService;

    @Test
    void get_admin_liefert_admin_seite() throws Exception {
        mvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    void get_addusers_liefert_addusers_seite() throws Exception {
        mvc.perform(get("/addusers"))
                .andExpect(status().isOk())
                .andExpect(view().name("addusers"))
                .andExpect(model().attributeExists("addUserForm"));
    }

    @Test
    void post_addusers_valid_redirect() throws Exception {
        mvc.perform(post("/addusers")
                        .with(csrf())
                        .param("githubid", "seema")
                        .param("role", "Student"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    void post_addusers_invalid_bleibt_auf_seite() throws Exception {
        mvc.perform(post("/addusers")
                        .with(csrf())
                        .param("role", "admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("addusers"))
                .andExpect(model().hasErrors());
    }

    @Test
    void get_addProfile_liefert_seite_mit_profilform() throws Exception {
        mvc.perform(get("/addProfile"))
                .andExpect(status().isOk())
                .andExpect(view().name("addProfile"))
                .andExpect(model().attributeExists("profileForm"));
    }

    @Test
    void post_addProfile_valid_redirect() throws Exception {
        mvc.perform(post("/addProfile")
                        .with(csrf())
                        .param("name", "Max ")
                        .param("fachgebiete", "AI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addProfile"));
        verify(addProfileService).addProfile(any());
    }

    @Test
    void post_addProfile_invalid_name_bleibt_auf_seite() throws Exception {
        mvc.perform(post("/addProfile")
                        .with(csrf())
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("addProfile"))
                .andExpect(model().hasErrors());
        verify(addProfileService, never()).addProfile(any());
    }

    @Test
    void get_addTopics_liefert_seite_mit_themeForm() throws Exception {
        mvc.perform(get("/addTopics"))
                .andExpect(status().isOk())
                .andExpect(view().name("addTopics"))
                .andExpect(model().attributeExists("themeForm"));
    }

    @Test
    void post_addTopics_valid_redirect() throws Exception {
        mvc.perform(post("/addTopics")
                        .with(csrf())
                        .param("titel", "A")
                        .param("beschreibungMarkdown", "B")
                        .param("fachgebiete", "C")
                        .param("requiredModules", "Propra 1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/addTopics"));
        verify(topicService).addTheme(any());
    }

    @Test
    void post_addTopics_invalid_titel_bleibt_auf_seite() throws Exception {
        mvc.perform(post("/addTopics")
                        .with(csrf())
                        .param("titel", "")
                        .param("beschreibungMarkdown", "B")
                        .param("fachgebiete", "C")
                        .param("requiredModules", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("addTopics"))
                .andExpect(model().hasErrors());
        verify(topicService, never()).addTheme(any());
    }
}
