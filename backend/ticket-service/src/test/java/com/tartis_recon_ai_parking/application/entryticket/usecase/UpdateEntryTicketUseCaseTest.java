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

    @InjectMocks
    private UpdateEntryTicketUseCase updateEntryTicketUseCase;

    @Captor
    private ArgumentCaptor<EntryTicket> ticketCaptor;

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

        when(entryTicketPersistence.findById(ticketId)).thenReturn(Optional.of(existing));
        when(entryTicketPersistence.save(any(EntryTicket.class))).thenAnswer(i -> i.getArgument(0));

        EntryTicketDTO result = updateEntryTicketUseCase.execute(ticketId, updateDTO);

        verify(entryTicketPersistence, times(1)).findById(ticketId);
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

        when(entryTicketPersistence.findById(ticketId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> updateEntryTicketUseCase.execute(ticketId, updateDTO))
                .isInstanceOf(EntryTicketNotFoundException.class)
                .hasMessageContaining("EntryTicket not found: " + ticketId);

        verify(entryTicketPersistence, times(1)).findById(ticketId);
        verify(entryTicketPersistence, never()).save(any());
    }
}
