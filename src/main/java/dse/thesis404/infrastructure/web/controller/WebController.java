package dse.thesis404.infrastructure.web.controller;

import dse.thesis404.infrastructure.web.form.AddUserForm;
import dse.thesis404.infrastructure.web.form.FachgebietForm;
import dse.thesis404.infrastructure.web.form.ProfileForm;
import dse.thesis404.infrastructure.web.form.ThemeForm;
import dse.thesis404.application.service.AddProfileService;
import dse.thesis404.application.service.AdminUserListService;
import dse.thesis404.application.service.TopicService;
import dse.thesis404.application.service.UserService;
import dse.thesis404.domain.model.theme.Veranstaltung;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

  private final UserService userService;
  private final TopicService topicService;
  private final AddProfileService addProfileService;
  private final AdminUserListService adminUserListService;

  public WebController(UserService userService, TopicService topicService, AddProfileService addProfileService,
                       AdminUserListService adminUserListService) {
    this.userService = userService;
    this.topicService = topicService;
    this.addProfileService = addProfileService;
    this.adminUserListService = adminUserListService;
  }

  
  List<String> knownFachgebiete = List.of("AI","Spring","SQL","Python","Java");
  List<Veranstaltung> knownVeranstaltungen = List.of(
          new Veranstaltung("Programmierung"),
          new Veranstaltung("Algorithmen und Datenstrukturen"),
          new Veranstaltung("Theoretische Informatik"),
          new Veranstaltung("Softwaretechnik"),
          new Veranstaltung("Datenbanksysteme"),
          new Veranstaltung("Rechnernetze"),
          new Veranstaltung("Betriebssysteme"),
          new Veranstaltung("Propra 1"),
          new Veranstaltung("Propra 2"),
          new Veranstaltung("Lineare Algebra"),
          new Veranstaltung("Analysis")
  );

  @GetMapping("/")
  public String index(){
    return "login";
  }
  @GetMapping("/login")
  public String login(){
    return "login";
  }

  @GetMapping("/private")
  public String privatePage() {
    return "private";
  }

  @GetMapping("/homeadmin")
  public String homeAdmin() {
    return "redirect:/admin";
  }

  @GetMapping("/admin")
  public String admin(Model model) {
    model.addAttribute("adminUsers", adminUserListService.getAdminUserRows());
    return "admin";
  }

  @GetMapping("/addProfile")
  public String addProfile (Model model){
    model.addAttribute("profileForm", new ProfileForm());
    return "addProfile";
  }

  @PostMapping("/addProfile")
  public String handleAddProfile(@Valid ProfileForm profileForm,
                                 BindingResult bindingResult,
                                 Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("profileForm", profileForm);
      return "addProfile";
    }
    addProfileService.addProfile(profileForm);
    return "redirect:/addProfile";
  }

  @GetMapping("/addTopics")
  public String addTopics(Model model){
    model.addAttribute("themeForm", new ThemeForm());
    model.addAttribute("requiredModuleForm", new FachgebietForm());
    model.addAttribute("allFachgebiete", knownFachgebiete);
    model.addAttribute("allVeranstaltungen", knownVeranstaltungen);
    return "addTopics";
  }

  @PostMapping("/addTopics")
  public String handleAddTopics(@Valid ThemeForm themeForm, BindingResult bindingResult, Model model){
    if (bindingResult.hasErrors()){
      model.addAttribute("themeForm", themeForm);
      model.addAttribute("requiredModuleForm", new FachgebietForm());
      model.addAttribute("allFachgebiete", knownFachgebiete);
      model.addAttribute("allVeranstaltungen", knownVeranstaltungen);
      return "addTopics";
    }
    topicService.addTheme(themeForm);
    return "redirect:/addTopics";
  }

  @GetMapping("/addusers")
  public String showAddUserForm(Model model) {
    model.addAttribute("addUserForm", new AddUserForm());
    return "addusers";
  }

  @PostMapping("/addusers")
  public String handleAddUser(@Valid AddUserForm addUserForm, BindingResult bindingResult) {
    if(bindingResult.hasErrors()) {
      return "addusers";
    }
    userService.createUserFromForm(addUserForm);
    return "redirect:/admin";
  }

}
