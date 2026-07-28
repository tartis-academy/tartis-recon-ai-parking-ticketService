package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.input.rest.dto.request;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketRequestTest {

    @Test
    void shouldStoreStayIdCorrectly() {
        UUID stayId = UUID.randomUUID();
        EntryTicketRequest request = new EntryTicketRequest(stayId);
        assertThat(request.stayId()).isEqualTo(stayId);
    }

    @Test
    void shouldAllowNullStayId() {
        EntryTicketRequest request = new EntryTicketRequest(null);
        assertThat(request.stayId()).isNull();
    }

    @Test
    void shouldSupportEquality() {
        UUID stayId = UUID.randomUUID();
        EntryTicketRequest r1 = new EntryTicketRequest(stayId);
        EntryTicketRequest r2 = new EntryTicketRequest(stayId);
        assertThat(r1).isEqualTo(r2);
    }
}
