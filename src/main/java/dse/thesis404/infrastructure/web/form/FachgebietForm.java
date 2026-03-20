package dse.thesis404.infrastructure.web.form;

import jakarta.validation.constraints.NotBlank;

public class FachgebietForm {

    @NotBlank(message = "Bitte wählen Sie eine Option aus")
    private String selectedFachgebiet;

    public String getSelectedFachgebiet() {
        return selectedFachgebiet;
    }

    public void setSelectedFachgebiet(String selectedFachgebiet) {
        this.selectedFachgebiet = selectedFachgebiet;
    }
}
