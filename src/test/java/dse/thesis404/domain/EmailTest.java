package dse.thesis404.domain;

import dse.thesis404.domain.model.betreuer.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Email – Validierung")
class EmailTest {

    @Test
    void null_liefert_klare_fehlermeldung_statt_nullpointerexception() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("leer");
    }

    @Test
    void leer_wird_abgelehnt() {
        assertThatThrownBy(() -> new Email("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leerzeichen_werden_entfernt() {
        assertThat(new Email("  anna@hhu.de ").email()).isEqualTo("anna@hhu.de");
    }

    @Test
    void ungueltiges_format_wird_abgelehnt() {
        assertThatThrownBy(() -> new Email("keine-mail"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ungültige");
    }
}
