package com.tartis_recon_ai_parking.domain.entryticket;

import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntryTicketTest {

    @Test
    @DisplayName("Debe crear un EntryTicket generando automaticamente uniqueId, code e issuedAt")
    void create_generatesFieldsAutomatically() {
        UUID stayId = UUID.randomUUID();

        EntryTicket ticket = EntryTicket.create(stayId);

        assertThat(ticket).isNotNull();
        assertThat(ticket.getStayId()).isEqualTo(stayId);
        assertThat(ticket.getUniqueId()).isNotNull();
        assertThat(ticket.getCode()).isNotNull().isNotBlank();
        assertThat(ticket.getIssuedAt()).isNotNull();
    }

    @Test
    @DisplayName("Debe reconstruir un EntryTicket a partir de datos persistidos sin alterar los valores")
    void recreate_restoresExactValues() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "TEST-1234";

        EntryTicket ticket = EntryTicket.recreate(uniqueId, stayId, issuedAt, code);

        assertThat(ticket).isNotNull();
        assertThat(ticket.getUniqueId()).isEqualTo(uniqueId);
        assertThat(ticket.getStayId()).isEqualTo(stayId);
        assertThat(ticket.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(ticket.getCode()).isEqualTo(code);
    }

    @Test
    @DisplayName("Lanza excepcion al crear con stayId nulo")
    void create_throwsException_whenStayIdIsNull() {
        assertThatThrownBy(() -> EntryTicket.create(null))
                .isInstanceOf(InvalidEntryTicketException.class)
                .hasMessageContaining("stayId is null");
    }

    @Test
    @DisplayName("Lanza excepcion al reconstruir con uniqueId nulo")
    void recreate_throwsException_whenUniqueIdIsNull() {
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        assertThatThrownBy(() -> EntryTicket.recreate(null, stayId, issuedAt, "CODE"))
                .isInstanceOf(InvalidEntryTicketException.class)
                .hasMessageContaining("uniqueId is null");
    }

    @Test
    @DisplayName("Lanza excepcion al reconstruir con stayId nulo")
    void recreate_throwsException_whenStayIdIsNull() {
        UUID uniqueId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        assertThatThrownBy(() -> EntryTicket.recreate(uniqueId, null, issuedAt, "CODE"))
                .isInstanceOf(InvalidEntryTicketException.class)
                .hasMessageContaining("stayId is null");
    }

    @Test
    @DisplayName("Lanza excepcion al reconstruir con issuedAt nulo")
    void recreate_throwsException_whenIssuedAtIsNull() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();

        assertThatThrownBy(() -> EntryTicket.recreate(uniqueId, stayId, null, "CODE"))
                .isInstanceOf(InvalidEntryTicketException.class)
                .hasMessageContaining("issuedAt is null");
    }

    @Test
    @DisplayName("Lanza excepcion al reconstruir con code nulo")
    void recreate_throwsException_whenCodeIsNull() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();

        assertThatThrownBy(() -> EntryTicket.recreate(uniqueId, stayId, issuedAt, null))
                .isInstanceOf(InvalidEntryTicketException.class)
                .hasMessageContaining("code is null");
    }
}
