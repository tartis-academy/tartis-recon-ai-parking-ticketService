package com.tartis_recon_ai_parking.domain.entryticket;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketStatusTest {

    @Test
    void issued_shouldNotBeTerminal() {
        assertThat(EntryTicketStatus.ISSUED.isTerminal()).isFalse();
    }

    @Test
    void used_shouldBeTerminal() {
        assertThat(EntryTicketStatus.USED.isTerminal()).isTrue();
    }

    @Test
    void lost_shouldBeTerminal() {
        assertThat(EntryTicketStatus.LOST.isTerminal()).isTrue();
    }

    @Test
    void enumShouldHaveExactlyThreeValues() {
        assertThat(EntryTicketStatus.values()).hasSize(3);
    }

    @ParameterizedTest
    @EnumSource(value = EntryTicketStatus.class, names = {"USED", "LOST"})
    void terminalStates_shouldAllReturnTrue(EntryTicketStatus status) {
        assertThat(status.isTerminal()).isTrue();
    }

    @Test
    void valueOf_ISSUED_shouldReturnCorrectEnum() {
        assertThat(EntryTicketStatus.valueOf("ISSUED")).isEqualTo(EntryTicketStatus.ISSUED);
    }

    @Test
    void valueOf_USED_shouldReturnCorrectEnum() {
        assertThat(EntryTicketStatus.valueOf("USED")).isEqualTo(EntryTicketStatus.USED);
    }

    @Test
    void valueOf_LOST_shouldReturnCorrectEnum() {
        assertThat(EntryTicketStatus.valueOf("LOST")).isEqualTo(EntryTicketStatus.LOST);
    }
}
