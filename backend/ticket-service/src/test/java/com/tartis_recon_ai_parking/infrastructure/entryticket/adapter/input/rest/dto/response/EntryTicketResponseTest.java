package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.response;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketResponseTest {

    @Test
    void shouldStoreAllFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "CODE123456";

        EntryTicketResponse response = new EntryTicketResponse(id, stayId, issuedAt, code);

        assertThat(response.id()).isEqualTo(id);
        assertThat(response.stayId()).isEqualTo(stayId);
        assertThat(response.issuedAt()).isEqualTo(issuedAt);
        assertThat(response.code()).isEqualTo(code);
    }

    @Test
    void shouldSupportEquality() {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        EntryTicketResponse r1 = new EntryTicketResponse(id, stayId, issuedAt, "CODE");
        EntryTicketResponse r2 = new EntryTicketResponse(id, stayId, issuedAt, "CODE");

        assertThat(r1).isEqualTo(r2);
    }
}
