package com.tartis_recon_ai_parking.application.ticket.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketCreateDTOTest {

    @Test
    void shouldStoreStayIdCorrectly() {
        UUID stayId = UUID.randomUUID();
        TicketCreateDTO dto = new TicketCreateDTO(stayId, Instant.now(), BigDecimal.TEN);
        assertThat(dto.stayId()).isEqualTo(stayId);
    }

    @Test
    void shouldStoreIssuedAtCorrectly() {
        Instant issuedAt = Instant.parse("2026-01-15T10:30:00Z");
        TicketCreateDTO dto = new TicketCreateDTO(UUID.randomUUID(), issuedAt, BigDecimal.ONE);
        assertThat(dto.issuedAt()).isEqualTo(issuedAt);
    }

    @Test
    void shouldStoreTotalAmountCorrectly() {
        BigDecimal amount = new BigDecimal("99.99");
        TicketCreateDTO dto = new TicketCreateDTO(UUID.randomUUID(), Instant.now(), amount);
        assertThat(dto.totalAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void shouldAllowNullFields() {
        TicketCreateDTO dto = new TicketCreateDTO(null, null, null);
        assertThat(dto.stayId()).isNull();
        assertThat(dto.issuedAt()).isNull();
        assertThat(dto.totalAmount()).isNull();
    }

    @Test
    void shouldSupportEqualityForSameValues() {
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        BigDecimal amount = BigDecimal.TEN;
        TicketCreateDTO dto1 = new TicketCreateDTO(stayId, issuedAt, amount);
        TicketCreateDTO dto2 = new TicketCreateDTO(stayId, issuedAt, amount);
        assertThat(dto1).isEqualTo(dto2);
    }
}
