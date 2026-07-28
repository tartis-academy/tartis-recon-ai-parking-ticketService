package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketResponseTest {

    @Test
    void shouldBuildWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        BigDecimal amount = new BigDecimal("15.00");

        TicketResponse response = TicketResponse.builder()
                .id(id)
                .stayId(stayId)
                .issuedAt(issuedAt)
                .totalAmount(amount)
                .build();

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getStayId()).isEqualTo(stayId);
        assertThat(response.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        TicketResponse response = new TicketResponse(id, stayId, issuedAt, BigDecimal.ZERO);

        assertThat(response.getId()).isEqualTo(id);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}
