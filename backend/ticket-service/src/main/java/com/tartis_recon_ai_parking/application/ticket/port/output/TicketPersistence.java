package com.tartis_recon_ai_parking.application.ticket.port.output;

import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;

public interface TicketPersistence {
    Ticket save(Ticket ticket);

    Optional<Ticket> findById(UUID id);

    List<Ticket> findAll();

    Page<Ticket> findAllPaginated(int page, int size);
}
