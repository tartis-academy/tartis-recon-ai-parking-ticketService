package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TicketPersistenceMapper {

    TicketEntity toEntity(Ticket ticket);

    Ticket toDomain(TicketEntity entity);

}