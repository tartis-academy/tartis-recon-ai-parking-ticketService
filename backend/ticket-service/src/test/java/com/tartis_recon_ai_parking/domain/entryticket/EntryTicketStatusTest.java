package com.tartis_recon_ai_parking.domain.entryticket;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class EntryTicketStatusTest {

    @Test
    @DisplayName("ISSUED no es terminal: el ticket aun admite transiciones")
    void issuedNoEsTerminal() {
        assertThat(EntryTicketStatus.ISSUED.isTerminal()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = EntryTicketStatus.class, names = {"USED", "LOST"})
    @DisplayName("USED y LOST son terminales (IN-21: el ticket no se reutiliza)")
    void usedYLostSonTerminales(EntryTicketStatus status) {
        assertThat(status.isTerminal()).isTrue();
    }

    @Test
    @DisplayName("El enum expone exactamente los tres estados del modelo")
    void exponeLosTresEstados() {
        assertThat(EntryTicketStatus.values()).containsExactly(
                EntryTicketStatus.ISSUED,
                EntryTicketStatus.USED,
                EntryTicketStatus.LOST);
    }
}
