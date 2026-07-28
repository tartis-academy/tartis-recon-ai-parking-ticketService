package com.tartis_recon_ai_parking.application.ticket.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketDTOTest {

    @Test
    void shouldBuildWithAllFields() {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        BigDecimal amount = new BigDecimal("25.50");

        TicketDTO dto = TicketDTO.builder()
                .id(id)
                .stayId(stayId)
                .issuedAt(issuedAt)
                .totalAmount(amount)
                .build();

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getStayId()).isEqualTo(stayId);
        assertThat(dto.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(dto.getTotalAmount()).isEqualByComparingTo(amount);
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        TicketDTO dto = new TicketDTO(id, stayId, issuedAt, BigDecimal.ZERO);

        assertThat(dto.getId()).isEqualTo(id);
        assertThat(dto.getStayId()).isEqualTo(stayId);
        assertThat(dto.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldAllowNullFieldsViaBuilder() {
        TicketDTO dto = TicketDTO.builder().build();
        assertThat(dto.getId()).isNull();
        assertThat(dto.getStayId()).isNull();
        assertThat(dto.getIssuedAt()).isNull();
        assertThat(dto.getTotalAmount()).isNull();
    }
}
