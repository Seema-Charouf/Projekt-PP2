package dse.thesis404.application.service;

import dse.thesis404.infrastructure.web.form.AddUserForm;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public void createUserFromForm(AddUserForm form) {
    System.out.println("User mit githubid=" + form.getGithubid() + " role=" + form.getRole() + "wurde erstellt");
  }
}
