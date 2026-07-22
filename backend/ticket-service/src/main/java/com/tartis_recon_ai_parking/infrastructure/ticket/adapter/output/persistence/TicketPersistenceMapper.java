package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface TicketPersistenceMapper {

    TicketEntity toEntity(Ticket ticket);

    Ticket toDomain(TicketEntity entity);

    @ObjectFactory
    default Ticket createTicket(TicketEntity entity) {
        return Ticket.recreate(
            entity.getUniqueId(), 
            entity.getStayId(), 
            entity.getIssuedAt(), 
            entity.getTotalAmount()
        );
    }

}