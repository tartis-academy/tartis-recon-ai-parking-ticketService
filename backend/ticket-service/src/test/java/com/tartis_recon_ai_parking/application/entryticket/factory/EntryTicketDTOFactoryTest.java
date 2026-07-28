package com.tartis_recon_ai_parking.application.entryticket.factory;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EntryTicketDTOFactoryTest {

    @Test
    void toDTO_shouldMapAllFieldsFromDomainEntryTicket() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "TESTCODE01";

        EntryTicket ticket = EntryTicket.recreate(uniqueId, stayId, issuedAt, code);

        EntryTicketDTO dto = EntryTicketDTOFactory.toDTO(ticket);

        assertThat(dto.uniqueId()).isEqualTo(uniqueId);
        assertThat(dto.stayId()).isEqualTo(stayId);
        assertThat(dto.issuedAt()).isEqualTo(issuedAt);
        assertThat(dto.code()).isEqualTo(code);
    }

    @Test
    void toDomain_shouldCreateEntryTicketWithGeneratedFields() {
        UUID stayId = UUID.randomUUID();
        EntryTicketCreateDTO createDTO = new EntryTicketCreateDTO(stayId);

        EntryTicket result = EntryTicketDTOFactory.toDomain(createDTO);

        assertThat(result.getUniqueId()).isNotNull();
        assertThat(result.getStayId()).isEqualTo(stayId);
        assertThat(result.getCode()).isNotNull().isNotBlank();
        assertThat(result.getIssuedAt()).isNotNull();
    }

    @Test
    void toDomain_shouldThrowWhenStayIdIsNull() {
        EntryTicketCreateDTO createDTO = new EntryTicketCreateDTO(null);

        assertThatThrownBy(() -> EntryTicketDTOFactory.toDomain(createDTO))
                .isInstanceOf(InvalidEntryTicketException.class);
    }

    @Test
    void toDTOList_shouldMapMultipleEntryTickets() {
        EntryTicket t1 = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "CODE-A");
        EntryTicket t2 = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "CODE-B");

        List<EntryTicketDTO> result = EntryTicketDTOFactory.toDTOList(List.of(t1, t2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).uniqueId()).isEqualTo(t1.getUniqueId());
        assertThat(result.get(1).uniqueId()).isEqualTo(t2.getUniqueId());
    }

    @Test
    void toDTOList_shouldReturnEmptyListForEmptyInput() {
        List<EntryTicketDTO> result = EntryTicketDTOFactory.toDTOList(List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void toDTOList_shouldPreserveCodeForEachElement() {
        EntryTicket t1 = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "CODE-1");
        EntryTicket t2 = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "CODE-2");
        EntryTicket t3 = EntryTicket.recreate(UUID.randomUUID(), UUID.randomUUID(), Instant.now(), "CODE-3");

        List<EntryTicketDTO> result = EntryTicketDTOFactory.toDTOList(List.of(t1, t2, t3));

        assertThat(result).hasSize(3);
        assertThat(result.get(0).code()).isEqualTo("CODE-1");
        assertThat(result.get(1).code()).isEqualTo("CODE-2");
        assertThat(result.get(2).code()).isEqualTo("CODE-3");
    }
}
