package com.tartis_recon_ai_parking.application.entryticket.port.output;

import com.tartis_recon_ai_parking.domain.entryticket.EntryTicket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EntryTicketPersistence {
    EntryTicket save(EntryTicket entryTicket);
    Optional<EntryTicket> findById(UUID id);
    Optional<EntryTicket> findByCode(String code);   // ← lectura del codigo de barras en la salida
    Optional<EntryTicket> findByStayId(UUID stayId);
    List<EntryTicket> findAll();

    /** Lectura con bloqueo pesimista — solo para caminos que mutan la fila. */
    Optional<EntryTicket> findByIdForUpdate(UUID id);
}
