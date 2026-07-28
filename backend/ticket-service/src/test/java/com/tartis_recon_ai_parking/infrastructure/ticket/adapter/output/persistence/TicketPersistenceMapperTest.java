package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.output.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tartis_recon_ai_parking.domain.ticket.Ticket;
import com.tartis_recon_ai_parking.domain.ticket.exception.InvalidTicketException;

// NOTA: TicketPersistenceMapperImpl es generado en tiempo de compilacion por el
// procesador de anotaciones de MapStruct. Ejecuta un build de Maven si tu IDE
// no lo encuentra todavia en target/generated-sources/annotations.
class TicketPersistenceMapperTest {

    private TicketPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TicketPersistenceMapperImpl();
    }

    @Test
    void toEntity_ShouldMapAllFieldsFromDomainTicket() {
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        Ticket ticket = Ticket.create(stayId, issuedAt, BigDecimal.valueOf(20.00));

        TicketEntity entity = mapper.toEntity(ticket);

        assertThat(entity.getUniqueId()).isEqualTo(ticket.getUniqueId());
        assertThat(entity.getStayId()).isEqualTo(stayId);
        assertThat(entity.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(entity.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(20.00));
    }

    @Test
    void toDomain_ShouldRecreateTicketFromEntityUsingObjectFactory() {
        UUID uniqueId = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        TicketEntity entity = new TicketEntity();
        entity.setUniqueId(uniqueId);
        entity.setStayId(stayId);
        entity.setIssuedAt(issuedAt);
        entity.setTotalAmount(BigDecimal.valueOf(5.00));

        Ticket domain = mapper.toDomain(entity);

        assertThat(domain.getUniqueId()).isEqualTo(uniqueId);
        assertThat(domain.getStayId()).isEqualTo(stayId);
        assertThat(domain.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(domain.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(5.00));
    }

    // Caso borde: la @ObjectFactory delega en Ticket.recreate, que valida los
    // datos y lanza InvalidTicketException si algun campo obligatorio es null.
    @Test
    void toDomain_WhenEntityHasNullRequiredField_ShouldThrowInvalidTicketException() {
        TicketEntity entity = new TicketEntity();
        entity.setUniqueId(UUID.randomUUID());
        entity.setStayId(null);
        entity.setIssuedAt(Instant.now());
        entity.setTotalAmount(BigDecimal.ONE);

        assertThatThrownBy(() -> mapper.toDomain(entity))
                .isInstanceOf(InvalidTicketException.class);
    }
}
