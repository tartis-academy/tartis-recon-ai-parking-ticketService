package com.tartis_recon_ai_parking.domain.entryticket.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntryTicketNotFoundExceptionTest {

    @Test
    void shouldStoreMessageCorrectly() {
        EntryTicketNotFoundException ex = new EntryTicketNotFoundException("EntryTicket not found: abc");
        assertThat(ex.getMessage()).isEqualTo("EntryTicket not found: abc");
    }

    @Test
    void shouldBeRuntimeException() {
        EntryTicketNotFoundException ex = new EntryTicketNotFoundException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldBeCatchableAsRuntimeException() {
        assertThatThrownBy(() -> { throw new EntryTicketNotFoundException("not found"); })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("not found");
    }
}
