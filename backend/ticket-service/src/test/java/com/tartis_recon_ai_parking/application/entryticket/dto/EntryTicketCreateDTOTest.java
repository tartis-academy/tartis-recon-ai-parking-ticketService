package com.tartis_recon_ai_parking.application.entryticket.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketCreateDTOTest {

    @Test
    void shouldStoreStayIdCorrectly() {
        UUID stayId = UUID.randomUUID();
        EntryTicketCreateDTO dto = new EntryTicketCreateDTO(stayId);
        assertThat(dto.stayId()).isEqualTo(stayId);
    }

    @Test
    void shouldAllowNullStayId() {
        EntryTicketCreateDTO dto = new EntryTicketCreateDTO(null);
        assertThat(dto.stayId()).isNull();
    }

    @Test
    void shouldSupportEqualityForSameStayId() {
        UUID stayId = UUID.randomUUID();
        EntryTicketCreateDTO dto1 = new EntryTicketCreateDTO(stayId);
        EntryTicketCreateDTO dto2 = new EntryTicketCreateDTO(stayId);
        assertThat(dto1).isEqualTo(dto2);
    }

    @Test
    void shouldNotBeEqualForDifferentStayIds() {
        EntryTicketCreateDTO dto1 = new EntryTicketCreateDTO(UUID.randomUUID());
        EntryTicketCreateDTO dto2 = new EntryTicketCreateDTO(UUID.randomUUID());
        assertThat(dto1).isNotEqualTo(dto2);
    }
}
