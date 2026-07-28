package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TicketRequestTest {

    @Test
    void shouldCreateWithBuilder() {
        UUID stayId = UUID.randomUUID();
        TicketRequest request = TicketRequest.builder().stayId(stayId).build();
        assertThat(request.getStayId()).isEqualTo(stayId);
    }

    @Test
    void shouldCreateWithNoArgsConstructor() {
        TicketRequest request = new TicketRequest();
        assertThat(request.getStayId()).isNull();
    }

    @Test
    void shouldCreateWithAllArgsConstructor() {
        UUID stayId = UUID.randomUUID();
        TicketRequest request = new TicketRequest(stayId);
        assertThat(request.getStayId()).isEqualTo(stayId);
    }

    @Test
    void shouldSetStayIdViaSetter() {
        TicketRequest request = new TicketRequest();
        UUID stayId = UUID.randomUUID();
        request.setStayId(stayId);
        assertThat(request.getStayId()).isEqualTo(stayId);
    }
}
