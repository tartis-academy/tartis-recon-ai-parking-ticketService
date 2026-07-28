package com.tartis_recon_ai_parking.domain.ticket.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvalidTicketExceptionTest {

    @Test
    void shouldStoreMessageCorrectly() {
        InvalidTicketException ex = new InvalidTicketException("uniqueId is null");
        assertThat(ex.getMessage()).isEqualTo("uniqueId is null");
    }

    @Test
    void shouldBeRuntimeException() {
        InvalidTicketException ex = new InvalidTicketException("test");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldBeCatchableAsRuntimeException() {
        assertThatThrownBy(() -> { throw new InvalidTicketException("error"); })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("error");
    }
}
