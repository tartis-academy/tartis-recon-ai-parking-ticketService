package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.domain.ticket.exception.TicketAlreadyExistsException;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto.StayClosedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

@Component
public class TicketEventListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TicketEventListenerAdapter.class);

    private final CreateTicketUseCase createTicketUseCase;

    public TicketEventListenerAdapter(CreateTicketUseCase createTicketUseCase) {
        this.createTicketUseCase = createTicketUseCase;
    }

    @RabbitListener(queues = "ticket-service-stay-closed-queue")
    public void handleStayClosedEvent(StayClosedEvent event) throws InvalidTicketException {
        TicketCreateDTO createDTO = new TicketCreateDTO(
                event.data().stayId(),
                event.data().exitDate(),
                event.data().totalAmount()
        );
        try {
            createTicketUseCase.execute(createDTO);
            logger.info("Ticket created successfully for stayId: {}", event.data().stayId());
        } catch (TicketAlreadyExistsException e) {
            logger.warn("Idempotencia activada (comprobación previa): El ticket para stayId {} ya existe. Ignorando evento duplicado.", event.data().stayId());
        } catch (DataIntegrityViolationException e) {
            logger.warn("Idempotencia activada (restricción BD): Violación de unicidad para stayId {}. Ignorando evento concurrente.", event.data().stayId());
        }
    }
}
