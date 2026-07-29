package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.usecase.CreateTicketUseCase;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.eventlistener.dto.StayClosedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TicketEventListenerAdapter {

    private final CreateTicketUseCase createTicketUseCase;

    public TicketEventListenerAdapter(CreateTicketUseCase createTicketUseCase) {
        this.createTicketUseCase = createTicketUseCase;
    }

    @RabbitListener(queues = "ticket-service-stay-closed-queue")
    public void handleStayClosedEvent(StayClosedEvent event) throws InvalidTicketException {
        TicketCreateDTO createDTO = new TicketCreateDTO(
                event.data().stayId(),
                Instant.now(),
                event.data().totalAmount()
        );
        createTicketUseCase.execute(createDTO);
    }
}
