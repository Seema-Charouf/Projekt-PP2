package dse.thesis404.infrastructure.web.form;

import jakarta.validation.constraints.Email;

public class EditProfileForm {

    private String titel;

    private String name;

    @Email(message = "Bitte geben Sie eine gültige E-Mail-Adresse ein")
    private String email;

    private String phone;

    private String raum;

    public String getTitel() {
        return titel;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRaum() {
        return raum;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRaum(String raum) {
        this.raum = raum;
    }

    
    public boolean hasAnyValue() {
        return (name != null && !name.isBlank())
                || (email != null && !email.isBlank())
                || (phone != null && !phone.isBlank())
                || (raum != null && !raum.isBlank());
    }
}
