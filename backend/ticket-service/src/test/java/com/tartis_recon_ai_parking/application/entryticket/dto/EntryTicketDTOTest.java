package com.tartis_recon_ai_parking.application.entryticket.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketDTOTest {

    @Test
    void shouldStoreAllFieldsCorrectly() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "ABCDEF1234";

        EntryTicketDTO dto = new EntryTicketDTO(uniqueId, stayId, issuedAt, code);

        assertThat(dto.uniqueId()).isEqualTo(uniqueId);
        assertThat(dto.stayId()).isEqualTo(stayId);
        assertThat(dto.issuedAt()).isEqualTo(issuedAt);
        assertThat(dto.code()).isEqualTo(code);
    }

    @Test
    void shouldSupportEqualityForSameValues() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "TEST123456";

        EntryTicketDTO dto1 = new EntryTicketDTO(uniqueId, stayId, issuedAt, code);
        EntryTicketDTO dto2 = new EntryTicketDTO(uniqueId, stayId, issuedAt, code);

        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualForDifferentCodes() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        EntryTicketDTO dto1 = new EntryTicketDTO(uniqueId, stayId, issuedAt, "CODE-A");
        EntryTicketDTO dto2 = new EntryTicketDTO(uniqueId, stayId, issuedAt, "CODE-B");

        assertThat(dto1).isNotEqualTo(dto2);
    }
}
