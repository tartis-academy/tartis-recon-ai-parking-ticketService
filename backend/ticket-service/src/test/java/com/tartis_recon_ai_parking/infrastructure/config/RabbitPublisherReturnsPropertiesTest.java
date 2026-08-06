package com.tartis_recon_ai_parking.infrastructure.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * El cableado de {@link UnroutableEventLogger} se puede verificar con mocks, pero
 * eso solo demuestra que el cable se enchufo, no que llegue a algun sitio: el
 * callback no se ejecuta si faltan estas dos propiedades, y un mock del
 * RabbitTemplate no puede verlo.
 *
 * <p>De las dos, {@code publisher-returns} es la traicionera. Sin ella el
 * CachingConnectionFactory no envuelve los canales en PublisherCallbackChannel,
 * RabbitTemplate.addListener() no registra nada, y el basic.return que manda el
 * broker se descarta en el cliente: ni excepcion, ni log, ni callback. Justo el
 * fallo silencioso que la red de seguridad viene a evitar.
 *
 * <p>Se leen de src/main/resources y no del classpath porque el
 * application.properties de test lo tapa. Esto cubre que las lineas no
 * desaparezcan; que el ERROR salga de verdad hay que comprobarlo levantando el
 * stack y publicando sin consumidor.
 */
class RabbitPublisherReturnsPropertiesTest {

    private static final Path APPLICATION_PROPERTIES =
            Path.of("src", "main", "resources", "application.properties");

    private static final Properties PROPERTIES = new Properties();

    @BeforeAll
    static void cargarApplicationProperties() throws Exception {
        try (InputStream in = Files.newInputStream(APPLICATION_PROPERTIES)) {
            PROPERTIES.load(in);
        }
    }

    @Test
    void publisherReturnsDebeEstarActivadoOElCallbackNuncaSeEjecuta() {
        assertEquals("true", PROPERTIES.getProperty("spring.rabbitmq.publisher-returns"));
    }

    @Test
    void mandatoryDebeEstarActivadoOElBrokerNoDevuelveElMensaje() {
        assertEquals("true", PROPERTIES.getProperty("spring.rabbitmq.template.mandatory"));
    }
}
