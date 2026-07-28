package com.tartis_recon_ai_parking.application.entryticket.generator;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EntryTicketCodeGeneratorTest {

    private static final String VALID_CHARS = "23456789ABCDEFGHJKMNPQRSTVWXYZ";

    @Test
    void generate_shouldReturnNonNullNonBlankCode() {
        String code = EntryTicketCodeGenerator.generate();
        assertThat(code).isNotNull().isNotBlank();
    }

    @Test
    void generate_shouldReturnCodeWithExactLength10() {
        String code = EntryTicketCodeGenerator.generate();
        assertThat(code).hasSize(10);
    }

    @Test
    void generate_shouldContainOnlyAllowedCharacters() {
        String code = EntryTicketCodeGenerator.generate();
        for (char c : code.toCharArray()) {
            assertThat(VALID_CHARS).contains(String.valueOf(c));
        }
    }

    @Test
    void generate_shouldNotContainAmbiguousCharacters() {
        String code = EntryTicketCodeGenerator.generate();
        assertThat(code).doesNotContain("0", "O", "1", "I", "L", "U");
    }

    @RepeatedTest(5)
    void generate_shouldProduceUniqueCodesOnRepeatedCalls() {
        String code1 = EntryTicketCodeGenerator.generate();
        String code2 = EntryTicketCodeGenerator.generate();
        assertThat(code1).isNotEqualTo(code2);
    }

    @Test
    void generate_shouldReturnUppercaseCode() {
        String code = EntryTicketCodeGenerator.generate();
        assertThat(code).isEqualTo(code.toUpperCase());
    }

    @Test
    void generate_shouldNotContainLowercaseLetters() {
        String code = EntryTicketCodeGenerator.generate();
        assertThat(code).doesNotContainPattern("[a-z]");
    }
}
