package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketEntityTest {

    @Test
    void shouldSetAndGetUniqueId() {
        TicketEntity entity = new TicketEntity();
        UUID id = UUID.randomUUID();
        entity.setUniqueId(id);
        assertThat(entity.getUniqueId()).isEqualTo(id);
    }

    @Test
    void shouldSetAndGetStayId() {
        TicketEntity entity = new TicketEntity();
        UUID stayId = UUID.randomUUID();
        entity.setStayId(stayId);
        assertThat(entity.getStayId()).isEqualTo(stayId);
    }

    @Test
    void shouldSetAndGetIssuedAt() {
        TicketEntity entity = new TicketEntity();
        Instant issuedAt = Instant.now();
        entity.setIssuedAt(issuedAt);
        assertThat(entity.getIssuedAt()).isEqualTo(issuedAt);
    }

    @Test
    void shouldSetAndGetTotalAmount() {
        TicketEntity entity = new TicketEntity();
        BigDecimal amount = new BigDecimal("42.50");
        entity.setTotalAmount(amount);
        assertThat(entity.getTotalAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void defaultConstructor_shouldCreateEntityWithNullFields() {
        TicketEntity entity = new TicketEntity();
        assertThat(entity.getUniqueId()).isNull();
        assertThat(entity.getStayId()).isNull();
        assertThat(entity.getIssuedAt()).isNull();
        assertThat(entity.getTotalAmount()).isNull();
    }
}
