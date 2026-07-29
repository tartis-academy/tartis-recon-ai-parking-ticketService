package com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tartis_recon_ai_parking.application.ticket.dto.TicketCreateDTO;
import com.tartis_recon_ai_parking.application.ticket.dto.TicketDTO;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.request.TicketRequest;
import com.tartis_recon_ai_parking.infrastructure.ticket.adapter.input.rest.dto.response.TicketResponse;

// NOTA: TicketRestMapperImpl es generado en tiempo de compilacion por el
// procesador de anotaciones de MapStruct (mvn compile / test-compile).
// Si tu IDE marca esta clase como inexistente, ejecuta un build de Maven
// para que se genere en target/generated-sources/annotations.
class TicketRestMapperTest {

    private TicketRestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TicketRestMapperImpl();
    }

    @Test
    void toCreateDTO_ShouldMapStayIdAndDefaultTotalAmountToZero() {
        UUID stayId = UUID.randomUUID();
        TicketRequest request = TicketRequest.builder().stayId(stayId).build();

        Instant before = Instant.now();
        TicketCreateDTO result = mapper.toCreateDTO(request);
        Instant after = Instant.now();

        assertThat(result.stayId()).isEqualTo(stayId);
        assertThat(result.totalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(result.issuedAt()).isBetween(before, after);
    }

    @Test
    void toResponse_ShouldMapAllFieldsFromDTO() {
        UUID id = UUID.randomUUID();
        UUID stayId = UUID.randomUUID();
        Instant issuedAt = Instant.now();
        TicketDTO dto = TicketDTO.builder()
                .uniqueId(id)
                .stayId(stayId)
                .issuedAt(issuedAt)
                .totalAmount(BigDecimal.valueOf(15.50))
                .build();

        TicketResponse response = mapper.toResponse(dto);

        assertThat(response.getUniqueId()).isEqualTo(id);
        assertThat(response.getStayId()).isEqualTo(stayId);
        assertThat(response.getIssuedAt()).isEqualTo(issuedAt);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(15.50));
    }

    @Test
    void toResponseList_ShouldMapEachElementOfTheList() {
        TicketDTO dto1 = TicketDTO.builder()
                .uniqueId(UUID.randomUUID())
                .stayId(UUID.randomUUID())
                .issuedAt(Instant.now())
                .totalAmount(BigDecimal.ONE)
                .build();
        TicketDTO dto2 = TicketDTO.builder()
                .uniqueId(UUID.randomUUID())
                .stayId(UUID.randomUUID())
                .issuedAt(Instant.now())
                .totalAmount(BigDecimal.TEN)
                .build();

        List<TicketResponse> result = mapper.toResponseList(List.of(dto1, dto2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUniqueId()).isEqualTo(dto1.getUniqueId());
        assertThat(result.get(1).getUniqueId()).isEqualTo(dto2.getUniqueId());
    }
}
