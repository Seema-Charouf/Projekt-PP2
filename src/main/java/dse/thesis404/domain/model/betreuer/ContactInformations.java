package dse.thesis404.domain.model.betreuer;

public record ContactInformations(Email email, PhoneNumber phoneNumber, Raum raum) {
    
    public ContactInformations {
        if (email == null) {
            throw new IllegalArgumentException("Email darf nicht null sein!");
        }
        if (raum == null) {
            throw new IllegalArgumentException("Raum darf nicht null sein!");
        }
    }
    

    public ContactInformations withEmail(Email newEmail) {
        return new ContactInformations(newEmail, this.phoneNumber, this.raum);
    }
    public ContactInformations withPhoneNumber(PhoneNumber newPhoneNumber) {
        return new ContactInformations(this.email, newPhoneNumber, this.raum);
    }
    public ContactInformations withRaum(Raum newRaum) {
        return new ContactInformations(this.email, this.phoneNumber, newRaum);
    }
}
