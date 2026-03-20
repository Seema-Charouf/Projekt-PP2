package dse.thesis404.infrastructure.persistence.betreuer;

import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.testutil.BetreuerProfilMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@DisplayName("BetreuerProfilRepository – Save/Load")
class BetreuerProfilRepositoryTest {

    @Autowired
    private BetreuerProfilRepository repository;

    @Test
    void save_and_findById_roundtrip() {

        BetreuerProfil profil = BetreuerProfilMother.karlaTuring();

        repository.save(profil);
        BetreuerProfil loaded = repository.findById(profil.getId()).orElseThrow();

        assertThat(loaded.getName()).isEqualTo("Karla Turing");
        assertThat(loaded.getFachgebiete()).extracting(f -> f.fachgebiet()).contains("Compilerbau");
    }

    @Test
    void findByGithubId_liefert_profil() {

        BetreuerProfil profil = BetreuerProfilMother.withGithubId(3000);
        repository.save(profil);

        BetreuerProfil found = repository.findByGithubId(3000).orElseThrow();

        assertThat(found.getId().value()).isEqualTo(3000);
    }
}
