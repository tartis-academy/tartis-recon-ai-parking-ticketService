package com.tartis_recon_ai_parking.application.ticket.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;
import com.tartis_recon_ai_parking.domain.ticket.Ticket;

/**
 * Evento publicado en {@code ticket-changed-v1}.
 *
 * <p><b>Un mismo nombre de evento cubre hoy dos agregados distintos</b>, asi que
 * el payload cambia segun el origen. Quien lo consuma tiene que contar con esto:
 *
 * <pre>
 * origen                     ticketId                  code    amount   status
 * -------------------------------------------------------------------------------
 * EntryTicket (entrada)      EntryTicket.uniqueId      codigo  null     CREATED / UPDATED
 * Ticket      (recibo)       Ticket.uniqueId           null    importe  PAID
 * </pre>
 *
 * <p>Tres consecuencias que conviene tener presentes:
 *
 * <ul>
 *   <li><b>{@code ticketId} lleva identificadores de dos agregados distintos</b>
 *       (dos tablas: {@code entry_ticket} y {@code receipts}). Un consumidor que
 *       guarde ese UUID no puede resolverlo sin mirar antes {@code status}.</li>
 *   <li><b>{@code type} es siempre {@code "TicketChangedEvent"}</b>, asi que el
 *       unico discriminador real es {@code status}, un String libre. Si se anade
 *       un estado nuevo, un consumidor antiguo no sabra que campos esperar.</li>
 *   <li><b>{@code code} y {@code amount} son null la mitad de las veces</b>, de
 *       forma excluyente segun la fila de la tabla.</li>
 * </ul>
 *
 * <p>Esta forma es deliberadamente provisional. Lo correcto a medio plazo es
 * separarlo en {@code EntryTicketCreated} / {@code EntryTicketUpdated} /
 * {@code TicketPaid}, o como minimo mover el discriminador de {@code status} a
 * {@code type}. Mientras tanto, cualquier cambio en estos campos rompe a los
 * consumidores en silencio: {@code version} tiene que subir a {@code v2} y la
 * routing key a {@code ticket-changed-v2}.
 */
public record TicketChangedEvent(
    UUID eventId,
    String type,
    String version,
    Instant occurredAt,
    TicketChangedData data
) {
    /**
     * Campos del evento. Cuales vienen informados y cuales null depende del
     * origen: ver la tabla en el javadoc de {@link TicketChangedEvent}.
     *
     * @param ticketId  id del agregado que cambio; segun {@code status} es un
     *                  EntryTicket (CREATED/UPDATED) o un Ticket/recibo (PAID)
     * @param stayId    estancia a la que pertenece; siempre informado
     * @param code      codigo del ticket de entrada; null en el recibo
     * @param issuedAt  fecha de emision del agregado; siempre informado
     * @param status    CREATED | UPDATED | PAID (unico discriminador del payload)
     * @param amount    importe total del recibo; null en el ticket de entrada
     */
    public record TicketChangedData(
        UUID ticketId,
        UUID stayId,
        String code,
        Instant issuedAt,
        String status,
        BigDecimal amount
    ) {}

    public static TicketChangedEvent of(EntryTicket ticket, String status, Instant occurredAt) {
        return new TicketChangedEvent(
            UUID.randomUUID(),
            "TicketChangedEvent",
            "v1",
            occurredAt,
            new TicketChangedData(
                ticket.getUniqueId(),
                ticket.getStayId(),
                ticket.getCode(),
                ticket.getIssuedAt(),
                status != null ? status : "CREATED",
                null
            )
        );
    }

    public static TicketChangedEvent of(EntryTicket ticket, Instant occurredAt) {
        return of(ticket, "CREATED", occurredAt);
    }

    public static TicketChangedEvent of(Ticket receipt, Instant occurredAt) {
        return new TicketChangedEvent(
            UUID.randomUUID(),
            "TicketChangedEvent",
            "v1",
            occurredAt,
            new TicketChangedData(
                receipt.getUniqueId(),
                receipt.getStayId(),
                null,
                receipt.getIssuedAt(),
                "PAID",
                receipt.getTotalAmount()
            )
        );
    }
}
