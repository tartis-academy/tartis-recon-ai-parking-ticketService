package com.tartis_recon_ai_parking.domain.ticket;

import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Ticket.
 *
 * No requiere mocks: Ticket no tiene dependencias externas, solo se prueban
 * sus factory methods estáticos (create/recreate) y las validaciones.
 */
class TicketTest {

    private final UUID stayId = UUID.randomUUID();
    private final Instant issuedAt = Instant.now();
    private final BigDecimal totalAmount = new BigDecimal("15.50");

    // ---------- create() ----------

    @Test
    void create_deberiaGenerarTicketConUniqueIdAleatorio_cuandoLosDatosSonValidos() {
        Ticket ticket = Ticket.create(stayId, issuedAt, totalAmount);

        assertNotNull(ticket.getUniqueId());
        assertEquals(stayId, ticket.getStayId());
        assertEquals(issuedAt, ticket.getIssuedAt());
        assertEquals(totalAmount, ticket.getTotalAmount());
    }

    @Test
    void create_deberiaGenerarUnUniqueIdDistintoEnCadaLlamada() {
        Ticket ticket1 = Ticket.create(stayId, issuedAt, totalAmount);
        Ticket ticket2 = Ticket.create(stayId, issuedAt, totalAmount);

        assertNotEquals(ticket1.getUniqueId(), ticket2.getUniqueId());
    }

    @Test
    void create_deberiaLanzarInvalidTicketException_cuandoStayIdEsNull() {
        InvalidTicketException ex = assertThrows(InvalidTicketException.class,
                () -> Ticket.create(null, issuedAt, totalAmount));
        assertEquals("stayId is null", ex.getMessage());
    }

    @Test
    void create_deberiaLanzarInvalidTicketException_cuandoIssuedAtEsNull() {
        InvalidTicketException ex = assertThrows(InvalidTicketException.class,
                () -> Ticket.create(stayId, null, totalAmount));
        assertEquals("issuedAt is null", ex.getMessage());
    }

    @Test
    void create_deberiaLanzarInvalidTicketException_cuandoTotalAmountEsNull() {
        InvalidTicketException ex = assertThrows(InvalidTicketException.class,
                () -> Ticket.create(stayId, issuedAt, null));
        assertEquals("totalAmount is null", ex.getMessage());
    }

    // ---------- recreate() ----------

    @Test
    void recreate_deberiaReconstruirTicketConLosMismosDatos_cuandoSonValidos() {
        UUID uniqueId = UUID.randomUUID();

        Ticket ticket = Ticket.recreate(uniqueId, stayId, issuedAt, totalAmount);

        assertEquals(uniqueId, ticket.getUniqueId());
        assertEquals(stayId, ticket.getStayId());
        assertEquals(issuedAt, ticket.getIssuedAt());
        assertEquals(totalAmount, ticket.getTotalAmount());
    }

    @Test
    void recreate_deberiaLanzarInvalidTicketException_cuandoUniqueIdEsNull() {
        InvalidTicketException ex = assertThrows(InvalidTicketException.class,
                () -> Ticket.recreate(null, stayId, issuedAt, totalAmount));
        assertEquals("uniqueId is null", ex.getMessage());
    }

    @Test
    void recreate_deberiaLanzarInvalidTicketException_cuandoStayIdEsNull() {
        InvalidTicketException ex = assertThrows(InvalidTicketException.class,
                () -> Ticket.recreate(UUID.randomUUID(), null, issuedAt, totalAmount));
        assertEquals("stayId is null", ex.getMessage());
    }

    @Test
    void recreate_deberiaLanzarInvalidTicketException_cuandoIssuedAtEsNull() {
        InvalidTicketException ex = assertThrows(InvalidTicketException.class,
                () -> Ticket.recreate(UUID.randomUUID(), stayId, null, totalAmount));
        assertEquals("issuedAt is null", ex.getMessage());
    }

    @Test
    void recreate_deberiaLanzarInvalidTicketException_cuandoTotalAmountEsNull() {
        InvalidTicketException ex = assertThrows(InvalidTicketException.class,
                () -> Ticket.recreate(UUID.randomUUID(), stayId, issuedAt, null));
        assertEquals("totalAmount is null", ex.getMessage());
    }
}