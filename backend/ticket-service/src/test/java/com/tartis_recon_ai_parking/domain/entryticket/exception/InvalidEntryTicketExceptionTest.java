package com.tartis_recon_ai_parking.domain.entryticket.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvalidEntryTicketExceptionTest {

    @Test
    void shouldStoreMessageCorrectly() {
        InvalidEntryTicketException ex = new InvalidEntryTicketException("stayId is null");
        assertThat(ex.getMessage()).isEqualTo("stayId is null");
    }

    @Test
    void shouldBeRuntimeException() {
        InvalidEntryTicketException ex = new InvalidEntryTicketException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldBeCatchableAsRuntimeException() {
        assertThatThrownBy(() -> { throw new InvalidEntryTicketException("invalid"); })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("invalid");
    }
}
