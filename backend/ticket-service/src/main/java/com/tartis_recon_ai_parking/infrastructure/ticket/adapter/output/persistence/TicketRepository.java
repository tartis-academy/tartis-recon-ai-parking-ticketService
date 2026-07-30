package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.UUID;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<TicketEntity, UUID> {

    Optional<TicketEntity> findById(UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TicketEntity t where t.id = :id")
    Optional<TicketEntity> findByIdForUpdate(@Param("id") UUID id);

    @Query("SELECT t FROM TicketEntity t WHERE " +
          "(:search IS NULL OR CAST(t.uniqueId AS string) LIKE %:search% OR CAST(t.stayId AS string) LIKE %:search%)")
    Page<TicketEntity> findByFilters(
           @Param("search") String search,
           Pageable pageable);

    boolean existsByStayId(UUID stayId);
}
