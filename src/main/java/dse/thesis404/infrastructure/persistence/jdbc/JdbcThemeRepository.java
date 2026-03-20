package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.application.port.ThemeRepository;
import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.domain.model.shared.ProfileId;
import dse.thesis404.domain.model.theme.Beschreibung;
import dse.thesis404.domain.model.theme.Theme;
import dse.thesis404.domain.model.theme.ThemeId;
import dse.thesis404.infrastructure.persistence.jdbc.entity.ThemeAggregate;
import dse.thesis404.infrastructure.persistence.jdbc.entity.DateiEntity;
import dse.thesis404.infrastructure.persistence.jdbc.entity.ThemeDateiRef;
import dse.thesis404.infrastructure.persistence.jdbc.entity.ThemeFachgebiet;
import dse.thesis404.infrastructure.persistence.jdbc.entity.ThemeLink;
import dse.thesis404.infrastructure.persistence.jdbc.entity.ThemeVeranstaltung;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public class JdbcThemeRepository implements ThemeRepository {

    private final ThemeDataRepository themeDataRepository;
    private final DateiDataRepository dateiDataRepository;

    public JdbcThemeRepository(
            ThemeDataRepository themeDataRepository,
            DateiDataRepository dateiDataRepository
    ) {
        this.themeDataRepository = themeDataRepository;
        this.dateiDataRepository = dateiDataRepository;
    }

    @Override
    public Optional<Theme> findById(ThemeId id) {
        return themeDataRepository.findById(id.value())
                .map(this::toDomain);
    }

    @Override
    public List<Theme> findAll() {
        List<Theme> result = new ArrayList<>();
        themeDataRepository.findAll().forEach(agg -> result.add(toDomain(agg)));
        return result;
    }

    @Override
    public List<Theme> findByOwnerProfileId(ProfileId ownerProfileId) {
        return StreamSupport.stream(themeDataRepository.findAll().spliterator(), false)
                .filter(agg -> Objects.equals(ownerProfileId.value(), agg.getOwnerProfileId()))
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Theme theme) {
        for (Datei file : theme.getFiles()) {
            dateiDataRepository.save(DateiEntity.fromDomain(file));
        }
        ThemeAggregate agg = toAggregate(theme);
        agg.setNew(!themeDataRepository.existsById(theme.getId().value()));
        themeDataRepository.save(agg);
    }

    @Override
    public void delete(ThemeId id) {
        themeDataRepository.deleteById(id.value());
    }

    @Override
    public boolean exists(ThemeId id) {
        return themeDataRepository.existsById(id.value());
    }

    private Theme toDomain(ThemeAggregate agg) {
        Theme theme = new Theme(
                new ThemeId(agg.getThemeId()),
                new ProfileId(agg.getOwnerProfileId()),
                agg.getTitel(),
                new Beschreibung(agg.getBeschreibung() != null ? agg.getBeschreibung() : "")
        );

        if (agg.getFachgebiete() != null) {
            agg.getFachgebiete().stream()
                    .map(ThemeFachgebiet::getFachgebiet)
                    .filter(f -> f != null && !f.isBlank())
                    .forEach(theme::addFachgebiet);
        }
        if (agg.getVeranstaltungen() != null) {
            agg.getVeranstaltungen().stream()
                    .map(ThemeVeranstaltung::getName)
                    .filter(n -> n != null && !n.isBlank())
                    .forEach(theme::addVeranstaltung);
        }
        if (agg.getLinks() != null) {
            agg.getLinks().stream()
                    .filter(l -> l.getUrl() != null && l.getLinkText() != null)
                    .forEach(l -> theme.addLink(l.getUrl(), l.getLinkText()));
        }
        if (agg.getFileRefs() != null && !agg.getFileRefs().isEmpty()) {
            List<UUID> fileIds = agg.getFileRefs().stream()
                    .map(ThemeDateiRef::getFileId)
                    .toList();
            dateiDataRepository.findAllById(fileIds).forEach(e -> theme.addFile(e.toDomain()));
        }

        return theme;
    }

    private ThemeAggregate toAggregate(Theme theme) {
        ThemeAggregate agg = new ThemeAggregate();
        agg.setThemeId(theme.getId().value());
        agg.setOwnerProfileId(theme.getOwnerProfileId().value());
        agg.setTitel(theme.getTitel());
        agg.setBeschreibung(theme.getBeschreibung().toString());

        Set<ThemeFachgebiet> fachgebiete = theme.getFachgebiete().stream()
                .map(fg -> new ThemeFachgebiet(fg.fachgebiet()))
                .collect(Collectors.toSet());
        agg.setFachgebiete(fachgebiete);

        Set<ThemeVeranstaltung> veranstaltungen = theme.getAngeforderteVeranstaltungen().stream()
                .map(v -> new ThemeVeranstaltung(v.name()))
                .collect(Collectors.toSet());
        agg.setVeranstaltungen(veranstaltungen);

        Set<ThemeLink> links = theme.getLinks().stream()
                .map(l -> new ThemeLink(l.url().toString(), l.text()))
                .collect(Collectors.toSet());
        agg.setLinks(links);

        Set<ThemeDateiRef> fileRefs = theme.getFiles().stream()
                .map(f -> new ThemeDateiRef(f.id().value()))
                .collect(Collectors.toSet());
        agg.setFileRefs(fileRefs);

        return agg;
    }
}
