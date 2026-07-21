package com.tartis_recon_ai_parking.application.entryticket.generator;

import java.security.SecureRandom;

public final class EntryTicketCodeGenerator {

    private static final String ALPHABET = "23456789ABCDEFGHJKMNPQRSTVWXYZ"; // sin 0/O/1/I/L/U
    private static final int LENGTH = 10;
    private static final SecureRandom RANDOM = new SecureRandom();

    private EntryTicketCodeGenerator() {}

    public static String generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
