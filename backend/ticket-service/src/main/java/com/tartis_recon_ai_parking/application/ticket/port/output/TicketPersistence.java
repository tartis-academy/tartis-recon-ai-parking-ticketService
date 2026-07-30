package com.tartis_recon_ai_parking.application.ticket.port.output;

import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface TicketPersistence {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(UUID id);

    Optional<Ticket> findByIdForUpdate(UUID id);

    List<Ticket> findAll();

    TicketPage findPage(String search, int page, int size);

    boolean existsByStayId(UUID stayId);

    record TicketPage(List<Ticket> content, int page, int size, long totalElements, int totalPages) {}
}
