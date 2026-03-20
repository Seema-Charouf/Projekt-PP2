package dse.thesis404.infrastructure.web.form;
import jakarta.validation.constraints.NotBlank;

public class AddUserForm {

  @NotBlank(message = "GitHub Name darf nicht leer sein")
  private String githubid;
  @NotBlank(message = "Role muss ausgewählt werden")
  private String role;

  public String getGithubid() {
    return githubid;
  }

  public void setGithubid(String githubid) {
    this.githubid = githubid;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
