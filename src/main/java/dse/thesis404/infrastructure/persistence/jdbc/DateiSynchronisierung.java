package dse.thesis404.infrastructure.persistence.jdbc;

import dse.thesis404.domain.model.shared.Datei;
import dse.thesis404.infrastructure.persistence.jdbc.entity.DateiEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Hält die Tabelle "datei" mit den Datei-Listen von Theme und BetreuerProfil synchron.
 * Datei-Metadaten sind unveränderlich: neue werden eingefügt, entfernte gelöscht, bestehende nicht angefasst.
 */
@Component
public class DateiSynchronisierung {

    private final DateiDataRepository dateiDataRepository;

    DateiSynchronisierung(DateiDataRepository dateiDataRepository) {
        this.dateiDataRepository = dateiDataRepository;
    }

    /** Vor dem Speichern des Aggregats aufrufen (die Verknüpfungstabelle braucht die Datei-Zeile). */
    void neueEinfuegen(Collection<Datei> aktuell, Set<UUID> vorher) {
        for (Datei d : aktuell) {
            UUID id = d.id().value();
            if (!vorher.contains(id) && !dateiDataRepository.existsById(id)) {
                dateiDataRepository.save(DateiEntity.fromDomain(d).alsNeu());
            }
        }
    }

    /** Nach dem Speichern des Aggregats aufrufen: Datei-Zeilen löschen, die nicht mehr referenziert werden. */
    void entfernteLoeschen(Set<UUID> vorher, Collection<Datei> aktuell) {
        Set<UUID> nochDa = aktuell.stream().map(d -> d.id().value()).collect(Collectors.toSet());
        Set<UUID> entfernt = vorher.stream().filter(id -> !nochDa.contains(id)).collect(Collectors.toSet());
        if (!entfernt.isEmpty()) {
            dateiDataRepository.deleteAllById(entfernt);
        }
    }

    void alleLoeschen(Set<UUID> ids) {
        if (!ids.isEmpty()) {
            dateiDataRepository.deleteAllById(ids);
        }
    }
}
