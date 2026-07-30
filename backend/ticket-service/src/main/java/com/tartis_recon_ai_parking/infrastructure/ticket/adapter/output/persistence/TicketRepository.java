package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {
     Optional<TicketEntity> findById(UUID id);

     @org.springframework.data.jpa.repository.Query("SELECT t FROM TicketEntity t WHERE " +
           "(:search IS NULL OR CAST(t.uniqueId AS string) LIKE %:search% OR CAST(t.stayId AS string) LIKE %:search%)")
     org.springframework.data.domain.Page<TicketEntity> findByFilters(
            @org.springframework.data.repository.query.Param("search") String search,
            org.springframework.data.domain.Pageable pageable);
}
