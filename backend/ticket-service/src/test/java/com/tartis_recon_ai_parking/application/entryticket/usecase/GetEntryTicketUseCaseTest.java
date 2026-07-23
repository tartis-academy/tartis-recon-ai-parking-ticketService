package com.tartis_recon_ai_parking.application.entryticket.usecase;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tartis_recon_ai_parking.application.entryticket.dto.EntryTicketDTO;
import com.tartis_recon_ai_parking.application.entryticket.port.output.EntryTicketPersistence;
import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.entryticket.exception.EntryTicketNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Caso de uso: test unitario con Mockito sobre el puerto de salida.
 * No levanta Spring ni necesita base de datos.
 */
@ExtendWith(MockitoExtension.class)
class GetEntryTicketUseCaseTest {

    @Mock
    private EntryTicketPersistence entryTicketPersistence;

    @InjectMocks
    private GetEntryTicketUseCase getEntryTicketUseCase;

    @Test
    @DisplayName("Debe devolver el DTO del ticket cuando el id existe")
    void shouldReturnEntryTicketWhenFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        String code = "ABC23456DE";

        EntryTicket ticket = EntryTicket.recreate(id, stayId, issuedAt, code);
        when(entryTicketPersistence.findById(id)).thenReturn(Optional.of(ticket));

        // ACT
        EntryTicketDTO result = getEntryTicketUseCase.execute(id);

        // ASSERT: el DTO conserva los cuatro campos del dominio
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.stayId()).isEqualTo(stayId);
        assertThat(result.code()).isEqualTo(code);
        assertThat(result.issuedAt()).isEqualTo(issuedAt);

        verify(entryTicketPersistence, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar EntryTicketNotFoundException cuando el id no existe")
    void shouldThrowWhenNotFound() {
        // ARRANGE
        UUID id = UUID.randomUUID();
        when(entryTicketPersistence.findById(id)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> getEntryTicketUseCase.execute(id))
                .isInstanceOf(EntryTicketNotFoundException.class);

        verify(entryTicketPersistence, times(1)).findById(id);
    }
}