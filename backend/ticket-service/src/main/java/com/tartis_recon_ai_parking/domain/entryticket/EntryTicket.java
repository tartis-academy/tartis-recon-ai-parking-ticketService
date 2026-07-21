package com.tartis_recon_ai_parking.domain.entryticket;

import java.time.Instant;
import java.util.UUID;
import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;

public class EntryTicket {

    private UUID uniqueId;
    private UUID stayId;
    private Instant issuedAt;
    private String code;

    private EntryTicket(UUID uniqueId, UUID stayId, Instant issuedAt, String code) {
        this.uniqueId = uniqueId;
        this.stayId = stayId;
        this.issuedAt = issuedAt;
        this.code = code;
    }

    private static void validateData(UUID uniqueId, UUID stayId, Instant issuedAt, String code) {
        if (uniqueId == null) throw new InvalidEntryTicketException("uniqueId is null");
        if (stayId == null) throw new InvalidEntryTicketException("stayId is null");
        if (issuedAt == null) throw new InvalidEntryTicketException("issuedAt is null");
        if (code == null) throw new InvalidEntryTicketException("code is null");
    }

    public static EntryTicket create(UUID stayId, Instant issuedAt, String code) {
        UUID uniqueId = UUID.randomUUID();
        validateData(uniqueId, stayId, issuedAt, code);
        return new EntryTicket(uniqueId, stayId, issuedAt, code);
    }

    public static EntryTicket recreate(UUID uniqueId, UUID stayId, Instant issuedAt, String code) {
        validateData(uniqueId, stayId, issuedAt, code);
        return new EntryTicket(uniqueId, stayId, issuedAt, code);
    }

    public UUID getUniqueId() { return uniqueId; }
    public UUID getStayId() { return stayId; }
    public Instant getIssuedAt() { return issuedAt; }
    public String getCode() { return code; }
}