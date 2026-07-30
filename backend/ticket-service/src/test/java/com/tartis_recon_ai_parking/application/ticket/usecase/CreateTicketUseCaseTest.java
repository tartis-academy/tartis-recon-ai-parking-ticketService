package com.tartis_recon_ai_parking.application.ticket.usecase;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.application.ticket.factory.TicketDTOFactory;
import com.tartis_recon_ai_parking.application.ticket.port.output.TicketPersistence;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CreateTicketUseCase.
 *
 * NOTA: TicketDTOFactory se mockea como clase estática porque sus métodos
 * (toDomain / toDTO) son estáticos. Esto requiere Mockito 5.x (mockito-core)
 * o la dependencia mockito-inline en versiones anteriores.
 */
@ExtendWith(MockitoExtension.class)
class CreateTicketUseCaseTest {

    @Mock
    private TicketPersistence ticketPersistence;

    @Mock
    private TicketCreateDTO createDTO;

    @Mock
    private Ticket ticketDomain;

    @Mock
    private Ticket savedTicket;

    @Mock
    private TicketDTO expectedDTO;

    private CreateTicketUseCase useCase;
    private MockedStatic<TicketDTOFactory> factoryMock;

    @BeforeEach
    void setUp() {
        useCase = new CreateTicketUseCase(ticketPersistence);
        factoryMock = mockStatic(TicketDTOFactory.class);
    }

    @AfterEach
    void tearDown() {
        factoryMock.close();
    }

    @Test
    void execute_deberiaCrearTicketYRetornarDTO_cuandoElCreateDTOEsValido() throws InvalidTicketException {
        // given
        UUID stayId = UUID.randomUUID();
        when(createDTO.stayId()).thenReturn(stayId);
        when(ticketPersistence.existsByStayId(stayId)).thenReturn(false);
        factoryMock.when(() -> TicketDTOFactory.toDomain(createDTO)).thenReturn(ticketDomain);
        when(ticketPersistence.save(ticketDomain)).thenReturn(savedTicket);
        factoryMock.when(() -> TicketDTOFactory.toDTO(savedTicket)).thenReturn(expectedDTO);

        // when
        TicketDTO result = useCase.execute(createDTO);

        // then
        assertNotNull(result);
        assertEquals(expectedDTO, result);
        verify(ticketPersistence, times(1)).save(ticketDomain);
        factoryMock.verify(() -> TicketDTOFactory.toDomain(createDTO));
        factoryMock.verify(() -> TicketDTOFactory.toDTO(savedTicket));
    }

    @Test
    void execute_deberiaPropagarInvalidTicketException_cuandoLaFactoryLanzaExcepcion() {
        // given
        UUID stayId = UUID.randomUUID();
        when(createDTO.stayId()).thenReturn(stayId);
        when(ticketPersistence.existsByStayId(stayId)).thenReturn(false);
        factoryMock.when(() -> TicketDTOFactory.toDomain(createDTO))
                .thenThrow(new InvalidTicketException("Datos de ticket inválidos"));

        // when / then
        assertThrows(InvalidTicketException.class, () -> useCase.execute(createDTO));
        verify(ticketPersistence, never()).save(any());
    }

    @Test
    void execute_deberiaPropagarExcepcion_cuandoFallaLaPersistencia() {
        // given
        UUID stayId = UUID.randomUUID();
        when(createDTO.stayId()).thenReturn(stayId);
        when(ticketPersistence.existsByStayId(stayId)).thenReturn(false);
        factoryMock.when(() -> TicketDTOFactory.toDomain(createDTO)).thenReturn(ticketDomain);
        when(ticketPersistence.save(ticketDomain)).thenThrow(new RuntimeException("Error de base de datos"));

        // when / then
        assertThrows(RuntimeException.class, () -> useCase.execute(createDTO));
    }

    @Test
    void execute_deberiaLanzarTicketAlreadyExistsException_cuandoElStayIdYaExiste() {
        // given
        UUID stayId = UUID.randomUUID();
        when(createDTO.stayId()).thenReturn(stayId);
        when(ticketPersistence.existsByStayId(stayId)).thenReturn(true);

        // when / then (Idempotencia en negocio)
        assertThrows(TicketAlreadyExistsException.class, () -> useCase.execute(createDTO));
        verify(ticketPersistence, never()).save(any());
    }
}