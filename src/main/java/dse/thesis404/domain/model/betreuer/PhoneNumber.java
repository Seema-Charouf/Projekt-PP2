package dse.thesis404.domain.model.betreuer;

public record PhoneNumber(String phoneNumber) {

    public PhoneNumber {    
        if (phoneNumber != null && !phoneNumber.isBlank()) {
            String cleaned = phoneNumber.trim().replaceAll("[\\s-]", "");
            
            if (!cleaned.matches("^\\+?[0-9]{10,15}$")) {
                throw new IllegalArgumentException("Ungültige Telefonnummer: " + phoneNumber);
            }
            phoneNumber = cleaned;
        }
    }

    @Override
    public String toString() {
        return phoneNumber;
    }
}
