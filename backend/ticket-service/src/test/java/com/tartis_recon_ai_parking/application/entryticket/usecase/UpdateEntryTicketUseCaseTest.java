package com.tartis_recon_ai_parking.application.entryticket.usecase;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketCreateDTO;
import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketChangedEvent;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketEventPublisher;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEntryTicketUseCaseTest {

    @Mock
    private EntryTicketPersistence entryTicketPersistence;

    @Mock
    private TicketEventPublisher ticketEventPublisher;

    @InjectMocks
    private UpdateEntryTicketUseCase updateEntryTicketUseCase;

    @Captor
    private ArgumentCaptor<EntryTicket> ticketCaptor;

    @Captor
    private ArgumentCaptor<TicketChangedEvent> eventCaptor;

    @Test
    @DisplayName("Debe actualizar un ticket de entrada existente y guardarlo correctamente")
    void shouldUpdateEntryTicketSuccessfully() {
        UUID ticketId = UUID.randomUUID();
        UUID oldStayId = UUID.randomUUID();
        UUID newStayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "CODE-123";

        EntryTicket existing = EntryTicket.recreate(ticketId, oldStayId, issuedAt, code);
        EntryTicketCreateDTO updateDTO = new EntryTicketCreateDTO(newStayId);

        when(entryTicketPersistence.findByIdForUpdate(ticketId)).thenReturn(Optional.of(existing));
        when(entryTicketPersistence.save(any(EntryTicket.class))).thenAnswer(i -> i.getArgument(0));

        EntryTicketDTO result = updateEntryTicketUseCase.execute(ticketId, updateDTO);

        verify(entryTicketPersistence, times(1)).findByIdForUpdate(ticketId);
        verify(entryTicketPersistence, times(1)).save(ticketCaptor.capture());

        EntryTicket capturedTicket = ticketCaptor.getValue();
        assertThat(capturedTicket.getUniqueId()).isEqualTo(ticketId);
        assertThat(capturedTicket.getStayId()).isEqualTo(newStayId);
        assertThat(capturedTicket.getCode()).isEqualTo(code);
        assertThat(capturedTicket.getIssuedAt()).isEqualTo(issuedAt);

        assertThat(result).isNotNull();
        assertThat(result.uniqueId()).isEqualTo(ticketId);
        assertThat(result.stayId()).isEqualTo(newStayId);
        assertThat(result.code()).isEqualTo(code);
    }

    @Test
    @DisplayName("Debe lanzar EntryTicketNotFoundException si el ticket no existe")
    void shouldThrowExceptionWhenEntryTicketNotFound() {
        UUID ticketId = UUID.randomUUID();
        EntryTicketCreateDTO updateDTO = new EntryTicketCreateDTO(UUID.randomUUID());

        when(entryTicketPersistence.findByIdForUpdate(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateEntryTicketUseCase.execute(ticketId, updateDTO))
                .isInstanceOf(EntryTicketNotFoundException.class)
                .hasMessageContaining("EntryTicket not found: " + ticketId);

        verify(entryTicketPersistence, times(1)).findByIdForUpdate(ticketId);
        verify(entryTicketPersistence, never()).save(any());
        verify(ticketEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Debe publicar un TicketChangedEvent con estado UPDATED tras actualizar")
    void shouldPublishTicketChangedEventOnUpdate() {
        UUID ticketId = UUID.randomUUID();
        UUID newStayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "CODE-123";

        EntryTicket existing = EntryTicket.recreate(ticketId, UUID.randomUUID(), issuedAt, code);
        when(entryTicketPersistence.findByIdForUpdate(ticketId)).thenReturn(Optional.of(existing));
        when(entryTicketPersistence.save(any(EntryTicket.class))).thenAnswer(i -> i.getArgument(0));

        updateEntryTicketUseCase.execute(ticketId, new EntryTicketCreateDTO(newStayId));

        verify(ticketEventPublisher).publish(eventCaptor.capture());
        TicketChangedEvent event = eventCaptor.getValue();
        assertThat(event.type()).isEqualTo("TicketChangedEvent");
        assertThat(event.version()).isEqualTo("v1");
        assertThat(event.data().ticketId()).isEqualTo(ticketId);
        assertThat(event.data().stayId()).isEqualTo(newStayId);
        assertThat(event.data().code()).isEqualTo(code);
        assertThat(event.data().issuedAt()).isEqualTo(issuedAt);
        assertThat(event.data().status()).isEqualTo("UPDATED");
        assertThat(event.data().amount()).isNull();
    }
}
