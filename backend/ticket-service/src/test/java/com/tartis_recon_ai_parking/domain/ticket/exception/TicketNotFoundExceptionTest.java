package com.tartis_recon_ai_parking.domain.ticket.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TicketNotFoundExceptionTest {

    @Test
    void shouldStoreMessageCorrectly() {
        TicketNotFoundException ex = new TicketNotFoundException("No existe un ticket con id abc");
        assertThat(ex.getMessage()).isEqualTo("No existe un ticket con id abc");
    }

    @Test
    void shouldBeRuntimeException() {
        TicketNotFoundException ex = new TicketNotFoundException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldBeCatchableAsRuntimeException() {
        assertThatThrownBy(() -> { throw new TicketNotFoundException("not found"); })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("not found");
    }
}
