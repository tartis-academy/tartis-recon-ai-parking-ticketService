package com.tartis_recon_ai_parking.application.entryticket.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

class EntryTicketCodeGeneratorTest {

    private static final String ALFABETO = "23456789ABCDEFGHJKMNPQRSTVWXYZ";
    private static final int LONGITUD = 10;

    @RepeatedTest(20)
    @DisplayName("Genera codigos de longitud fija")
    void generaCodigosDeLongitudFija() {
        assertThat(EntryTicketCodeGenerator.generate()).hasSize(LONGITUD);
    }

    @RepeatedTest(20)
    @DisplayName("Solo usa caracteres del alfabeto permitido")
    void soloUsaCaracteresPermitidos() {
        String code = EntryTicketCodeGenerator.generate();
        for (char c : code.toCharArray()) {
            assertThat(ALFABETO).contains(String.valueOf(c));
        }
    }

    @Test
    @DisplayName("Nunca contiene caracteres ambiguos al teclear (0/O, 1/I/L, U)")
    void nuncaContieneCaracteresAmbiguos() {
        for (int i = 0; i < 500; i++) {
            assertThat(EntryTicketCodeGenerator.generate())
                    .doesNotContain("0", "1", "I", "L", "O", "U");
        }
    }

    @Test
    @DisplayName("No colisiona en un volumen alto de generaciones")
    void noColisionaEnVolumen() {
        Set<String> codigos = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            codigos.add(EntryTicketCodeGenerator.generate());
        }
        assertThat(codigos).hasSize(1000);
    }
}
