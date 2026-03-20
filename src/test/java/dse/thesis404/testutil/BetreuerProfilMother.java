package dse.thesis404.testutil;

import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.betreuer.ContactInformations;
import dse.thesis404.domain.model.betreuer.Email;
import dse.thesis404.domain.model.betreuer.Raum;
import dse.thesis404.domain.model.shared.ProfileId;

public final class BetreuerProfilMother {

    private BetreuerProfilMother() {}

    public static BetreuerProfil karlaTuring() {
        ContactInformations kontakt = new ContactInformations(
                new Email("karla@hhu.de"),
                null,
                Raum.of("25.12.01.01"));
        BetreuerProfil profil = new BetreuerProfil(
                ProfileId.ofGithubUserId(2000),
                "Karla Turing",
                kontakt);
        profil.addFachgebiet("Compilerbau");
        return profil;
    }

    public static BetreuerProfil withGithubId(long githubId) {
        ContactInformations kontakt = new ContactInformations(
                new Email("test@hhu.de"),
                null,
                Raum.of("25.12.01.01"));
        BetreuerProfil profil = new BetreuerProfil(
                ProfileId.ofGithubUserId(githubId),
                "Test User",
                kontakt);
        profil.addFachgebiet("Testgebiet");
        return profil;
    }
}
