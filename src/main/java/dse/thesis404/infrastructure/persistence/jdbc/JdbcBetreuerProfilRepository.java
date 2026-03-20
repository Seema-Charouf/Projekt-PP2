package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.application.port.BetreuerProfilRepository;
import dse.thesis404.domain.model.betreuer.BetreuerProfil;
import dse.thesis404.domain.model.betreuer.ContactInformations;
import dse.thesis404.domain.model.betreuer.Email;
import dse.thesis404.domain.model.betreuer.PhoneNumber;
import dse.thesis404.domain.model.betreuer.Raum;
import dse.thesis404.domain.model.betreuer.Titel;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.infrastructure.persistence.jdbc.entity.BetreuerFachgebiet;
import dse.thesis404.infrastructure.persistence.jdbc.entity.BetreuerLink;
import dse.thesis404.infrastructure.persistence.jdbc.entity.BetreuerProfilAggregate;
import dse.thesis404.infrastructure.persistence.jdbc.entity.BetreuerProfilDateiRef;
import dse.thesis404.infrastructure.persistence.jdbc.entity.DateiEntity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JdbcBetreuerProfilRepository implements BetreuerProfilRepository {

    private final BetreuerProfilDataRepository betreuerProfilDataRepository;
    private final DateiDataRepository dateiDataRepository;

    public JdbcBetreuerProfilRepository(
            BetreuerProfilDataRepository betreuerProfilDataRepository,
            DateiDataRepository dateiDataRepository
    ) {
        this.betreuerProfilDataRepository = betreuerProfilDataRepository;
        this.dateiDataRepository = dateiDataRepository;
    }

    @Override
    public Optional<BetreuerProfil> findById(ProfileId id) {
        return betreuerProfilDataRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<BetreuerProfil> findByGithubId(long githubId) {
        return betreuerProfilDataRepository.findByGithubId(githubId)
                .map(this::toDomain);
    }

    @Override
    public List<BetreuerProfil> findAll() {
        List<BetreuerProfil> result = new ArrayList<>();
        betreuerProfilDataRepository.findAll().forEach(agg -> result.add(toDomain(agg)));
        return result;
    }

    @Override
    public void save(BetreuerProfil profil) {
        for (Datei file : profil.getFiles()) {
            dateiDataRepository.save(DateiEntity.fromDomain(file));
        }
        BetreuerProfilAggregate agg = toAggregate(profil);
        agg.setNew(!betreuerProfilDataRepository.existsById(profil.getId().value()));
        betreuerProfilDataRepository.save(agg);
    }

    @Override
    public void delete(ProfileId id) {
        betreuerProfilDataRepository.deleteById(id.value());
    }

    @Override
    public boolean exists(ProfileId id) {
        return betreuerProfilDataRepository.existsById(id.value());
    }

    private BetreuerProfil toDomain(BetreuerProfilAggregate agg) {
        Titel titel = agg.getTitel() != null ? Titel.valueOf(agg.getTitel()) : Titel.NONE;
        Email email = new Email(agg.getEmail());
        PhoneNumber phone = agg.getPhone() != null && !agg.getPhone().isBlank()
                ? new PhoneNumber(agg.getPhone()) : null;
        Raum raum = Raum.of(agg.getRaum());
        ContactInformations contactInfos = new ContactInformations(email, phone, raum);

        BetreuerProfil profil = new BetreuerProfil(
                new ProfileId(agg.getProfileId()),
                titel,
                agg.getName(),
                contactInfos
        );

        if (agg.getFachgebiete() != null) {
            agg.getFachgebiete().stream()
                    .map(BetreuerFachgebiet::getFachgebiet)
                    .filter(f -> f != null && !f.isBlank())
                    .forEach(profil::addFachgebiet);
        }
        if (agg.getLinks() != null) {
            agg.getLinks().stream()
                    .filter(l -> l.getUrl() != null && l.getLinkText() != null)
                    .forEach(l -> profil.addLink(l.getUrl(), l.getLinkText()));
        }
        if (agg.getFileRefs() != null && !agg.getFileRefs().isEmpty()) {
            List<java.util.UUID> fileIds = agg.getFileRefs().stream()
                    .map(BetreuerProfilDateiRef::getFileId)
                    .toList();
            dateiDataRepository.findAllById(fileIds).forEach(e -> profil.addFile(e.toDomain()));
        }

        return profil;
    }

    private BetreuerProfilAggregate toAggregate(BetreuerProfil profil) {
        BetreuerProfilAggregate agg = new BetreuerProfilAggregate();
        agg.setProfileId(profil.getId().value());
        agg.setGithubId(profil.getId().value());
        agg.setTitel(profil.getTitel().name());
        agg.setName(profil.getName());
        agg.setEmail(profil.getMail().email());
        agg.setPhone(profil.getPhoneNumber() != null ? profil.getPhoneNumber().phoneNumber() : null);
        agg.setRaum(profil.getRaum().toCode());

        Set<BetreuerFachgebiet> fachgebiete = profil.getFachgebiete().stream()
                .map(fg -> new BetreuerFachgebiet(fg.fachgebiet()))
                .collect(Collectors.toSet());
        agg.setFachgebiete(fachgebiete);

        Set<BetreuerLink> links = profil.getLinks().stream()
                .map(l -> new BetreuerLink(l.url().toString(), l.text()))
                .collect(Collectors.toSet());
        agg.setLinks(links);

        Set<BetreuerProfilDateiRef> fileRefs = profil.getFiles().stream()
                .map(f -> new BetreuerProfilDateiRef(f.id().value()))
                .collect(Collectors.toSet());
        agg.setFileRefs(fileRefs);

        return agg;
    }
}
