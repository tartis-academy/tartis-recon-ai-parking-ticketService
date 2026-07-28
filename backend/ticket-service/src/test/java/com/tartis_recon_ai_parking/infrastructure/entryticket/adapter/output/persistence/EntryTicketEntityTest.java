package com.tartis_recon_ai_parking.infrastructure.entryticket.adapter.output.persistence;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketEntityTest {

    @Test
    void shouldSetAndGetId() {
        EntryTicketEntity entity = new EntryTicketEntity();
        UUID id = UUID.randomUUID();
        entity.setId(id);
        assertThat(entity.getId()).isEqualTo(id);
    }

    @Test
    void shouldSetAndGetStayId() {
        EntryTicketEntity entity = new EntryTicketEntity();
        UUID stayId = UUID.randomUUID();
        entity.setStayId(stayId);
        assertThat(entity.getStayId()).isEqualTo(stayId);
    }

    @Test
    void shouldSetAndGetCode() {
        EntryTicketEntity entity = new EntryTicketEntity();
        entity.setCode("MYCODE1234");
        assertThat(entity.getCode()).isEqualTo("MYCODE1234");
    }

    @Test
    void shouldSetAndGetIssuedAt() {
        EntryTicketEntity entity = new EntryTicketEntity();
        Instant issuedAt = Instant.now();
        entity.setIssuedAt(issuedAt);
        assertThat(entity.getIssuedAt()).isEqualTo(issuedAt);
    }

    @Test
    void defaultState_shouldHaveNullFields() {
        EntryTicketEntity entity = new EntryTicketEntity();
        assertThat(entity.getId()).isNull();
        assertThat(entity.getStayId()).isNull();
        assertThat(entity.getCode()).isNull();
        assertThat(entity.getIssuedAt()).isNull();
    }
}
