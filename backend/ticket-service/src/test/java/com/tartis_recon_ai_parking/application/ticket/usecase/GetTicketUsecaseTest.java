package com.tartis_recon_ai_parking.application.ticket.usecase;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.factory.TicketDTOFactory;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GetTicketUseCase.
 *
 * NOTA: se asume que getById() lanza TicketNotFoundException cuando el
 * ticket no existe (según el import visible en el archivo). Ajusta el
 * nombre/paquete de la excepción si es distinto en tu proyecto.
 */
@ExtendWith(MockitoExtension.class)
class GetTicketUseCaseTest {

    @Mock
    private TicketPersistence persistence;

    @Mock
    private Ticket ticket;

    @Mock
    private TicketDTO expectedDTO;

    private GetTicketUseCase useCase;
    private MockedStatic<TicketDTOFactory> factoryMock;
    private UUID ticketId;

    @BeforeEach
    void setUp() {
        useCase = new GetTicketUseCase(persistence);
        factoryMock = mockStatic(TicketDTOFactory.class);
        ticketId = UUID.randomUUID();
    }

    @AfterEach
    void tearDown() {
        factoryMock.close();
    }

    @Test
    void getById_deberiaRetornarTicketDTO_cuandoElTicketExiste() {
        // given
        when(persistence.findById(ticketId)).thenReturn(Optional.of(ticket));
        factoryMock.when(() -> TicketDTOFactory.toDTO(ticket)).thenReturn(expectedDTO);

        // when
        TicketDTO result = useCase.getById(ticketId);

        // then
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(persistence, times(1)).findById(ticketId);
        factoryMock.verify(() -> TicketDTOFactory.toDTO(ticket));
    }

    @Test
    void getById_deberiaLanzarTicketNotFoundException_cuandoElTicketNoExiste() {
        // given
        when(persistence.findById(ticketId)).thenReturn(Optional.empty());

        // when / then
        assertThrows(TicketNotFoundException.class, () -> useCase.getById(ticketId));
        verify(persistence, times(1)).findById(ticketId);
        factoryMock.verifyNoInteractions();
    }
}
