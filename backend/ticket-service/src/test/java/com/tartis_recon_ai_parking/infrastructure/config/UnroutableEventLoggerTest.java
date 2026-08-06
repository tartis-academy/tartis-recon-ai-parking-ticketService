package com.tartis_recon_ai_parking.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * El valor de este callback esta en el log, no en el retorno, asi que lo unico
 * que se puede afirmar aqui es que acepta el mensaje devuelto y no revienta: se
 * ejecuta en el hilo de I/O de la conexion AMQP, donde una excepcion no la
 * recoge nadie.
 */
class UnroutableEventLoggerTest {

    private final UnroutableEventLogger callback = new UnroutableEventLogger();

    @Test
    void debeRegistrarElMensajeDevueltoSinPropagarError() {
        Message message = new Message("{}".getBytes());
        ReturnedMessage returned = new ReturnedMessage(
                message, 312, "NO_ROUTE", "parking-events-exchange", "ticket-changed-v1");

        assertThatCode(() -> callback.returnedMessage(returned)).doesNotThrowAnyException();
    }
}
