package com.tartis_recon_ai_parking.application.entryticket.usecase;

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
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateEntryTicketUseCaseTest {

    @Mock
    private EntryTicketPersistence entryTicketPersistence;

    @Mock
    private TicketEventPublisher ticketEventPublisher;

    @InjectMocks
    private CreateEntryTicketUseCase createEntryTicketUseCase;

    @Captor
    private ArgumentCaptor<EntryTicket> ticketCaptor;

    @Captor
    private ArgumentCaptor<TicketChangedEvent> eventCaptor;

    @Test
    @DisplayName("Debe instanciar el dominio y guardarlo correctamente a traves del puerto")
    void shouldCreateAndSaveEntryTicket() throws InvalidEntryTicketException {
        // QUE HACE:
        UUID stayId = UUID.randomUUID();

        EntryTicketCreateDTO createDTO = new EntryTicketCreateDTO(stayId);
        
        // Simular que save() devuelve el MISMO ticket que se le pasa
        when(entryTicketPersistence.save(any(EntryTicket.class))).thenAnswer(i -> i.getArgument(0));

        // ACT
        EntryTicketDTO result = createEntryTicketUseCase.execute(createDTO);

        // QUE DEBERIA HACER:
        // Validar que se ha mandado a guardar un Ticket con los campos correctos.
        verify(entryTicketPersistence, times(1)).save(ticketCaptor.capture());
        
        EntryTicket capturedTicket = ticketCaptor.getValue();
        assertThat(capturedTicket.getStayId()).isEqualTo(stayId);
        assertThat(capturedTicket.getCode()).isNotNull();
        assertThat(capturedTicket.getIssuedAt()).isNotNull();
        assertThat(capturedTicket.getUniqueId()).isNotNull();

        // Validar el resultado devuelto
        assertThat(result).isNotNull();
        assertThat(result.stayId()).isEqualTo(stayId);
        assertThat(result.code()).isEqualTo(capturedTicket.getCode());
        assertThat(result.uniqueId()).isEqualTo(capturedTicket.getUniqueId());
    }

    @Test
    @DisplayName("Debe propagar InvalidEntryTicketException si los parametros son invalidos (ej: stayId = null)")
    void shouldThrowExceptionWhenStayIdIsNull() {
        EntryTicketCreateDTO createDTO = new EntryTicketCreateDTO(null);
        
        assertThatThrownBy(() -> createEntryTicketUseCase.execute(createDTO))
                .isInstanceOf(InvalidEntryTicketException.class)
                .hasMessageContaining("stayId is null");

        // Validar que no se llega a guardar nada
        verify(entryTicketPersistence, never()).save(any());
        verify(ticketEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Debe publicar un TicketChangedEvent con estado CREATED y los datos del ticket guardado")
    void shouldPublishTicketChangedEventOnCreate() throws InvalidEntryTicketException {
        UUID stayId = UUID.randomUUID();
        EntryTicketCreateDTO createDTO = new EntryTicketCreateDTO(stayId);
        when(entryTicketPersistence.save(any(EntryTicket.class))).thenAnswer(i -> i.getArgument(0));

        createEntryTicketUseCase.execute(createDTO);

        verify(entryTicketPersistence).save(ticketCaptor.capture());
        verify(ticketEventPublisher).publish(eventCaptor.capture());

        EntryTicket saved = ticketCaptor.getValue();
        TicketChangedEvent event = eventCaptor.getValue();
        assertThat(event.type()).isEqualTo("TicketChangedEvent");
        assertThat(event.version()).isEqualTo("v1");
        assertThat(event.data().ticketId()).isEqualTo(saved.getUniqueId());
        assertThat(event.data().stayId()).isEqualTo(stayId);
        assertThat(event.data().code()).isEqualTo(saved.getCode());
        assertThat(event.data().issuedAt()).isEqualTo(saved.getIssuedAt());
        assertThat(event.data().status()).isEqualTo("CREATED");
        assertThat(event.data().amount()).isNull();
    }
}
