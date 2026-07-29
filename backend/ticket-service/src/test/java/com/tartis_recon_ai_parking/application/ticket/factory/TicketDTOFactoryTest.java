package com.tartis_recon_ai_parking.application.ticket.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;

class TicketDTOFactoryTest {

    @Test
    void toDTO_ShouldMapAllFieldsFromDomainTicket() {
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        Ticket ticket = Ticket.create(stayId, issuedAt, BigDecimal.valueOf(12.50));

        TicketDTO dto = TicketDTOFactory.toDTO(ticket);

        assertThat(dto.getUniqueId()).isEqualTo(ticket.getUniqueId());
        assertThat(dto.getStayId()).isEqualTo(stayId);
        assertThat(dto.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(dto.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(12.50));
    }

    @Test
    void toDTOList_ShouldMapEachTicketOfTheList() {
        Ticket ticket1 = Ticket.create(UUID.randomUUID(), Instant.now(), BigDecimal.ONE);
        Ticket ticket2 = Ticket.create(UUID.randomUUID(), Instant.now(), BigDecimal.TEN);

        List<TicketDTO> result = TicketDTOFactory.toDTOList(List.of(ticket1, ticket2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUniqueId()).isEqualTo(ticket1.getUniqueId());
        assertThat(result.get(1).getUniqueId()).isEqualTo(ticket2.getUniqueId());
    }

    @Test
    void toDTOList_WhenEmptyList_ShouldReturnEmptyList() {
        List<TicketDTO> result = TicketDTOFactory.toDTOList(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void toDomain_ShouldCreateNewTicketWithGeneratedUniqueId() {
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        TicketCreateDTO createDTO = new TicketCreateDTO(stayId, issuedAt, BigDecimal.valueOf(7.00));

        Ticket result = TicketDTOFactory.toDomain(createDTO);

        assertThat(result.getUniqueId()).isNotNull();
        assertThat(result.getStayId()).isEqualTo(stayId);
        assertThat(result.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(7.00));
    }

    // Caso borde: toDomain delega en Ticket.create, que valida los datos
    // y lanza InvalidTicketException si algun campo obligatorio es null.
    @Test
    void toDomain_WhenCreateDTOHasNullStayId_ShouldThrowInvalidTicketException() {
        TicketCreateDTO createDTO = new TicketCreateDTO(null, Instant.now(), BigDecimal.ONE);

        assertThatThrownBy(() -> TicketDTOFactory.toDomain(createDTO))
                .isInstanceOf(InvalidTicketException.class);
    }

    @Test
    void toDomain_WhenCreateDTOHasNullTotalAmount_ShouldThrowInvalidTicketException() {
        TicketCreateDTO createDTO = new TicketCreateDTO(UUID.randomUUID(), Instant.now(), null);

        assertThatThrownBy(() -> TicketDTOFactory.toDomain(createDTO))
                .isInstanceOf(InvalidTicketException.class);
    }
}
