package com.tartis_recon_ai_parking.domain.entryticket;

import java.time.Instant;
import java.util.UUID;

import com.tartis_recon_ai_parking.domain.entryticket.exception.InvalidEntryTicketException;


public class EntryTicket {
    // Pendiente de implementacion: id, stayId, code, issuedAt, usedAt, status
    // Pendiente de implementacion: issue(), use(), restore()  -> mismo patron inmutable que Stay
    private UUID uniqueId;
    private UUID stayId;
    private Instant issuedAt;
    private String code;

    // --- CONSTRUCTORES ---

    
        

    // Constructor con todos los parámetros
    private EntryTicket(UUID uniqueId, UUID stayId, Instant issuedAt, String code) {
        this.uniqueId = uniqueId;
        this.stayId = stayId;
        this.issuedAt = issuedAt;
        this.code = code;

    }

    static private void validateData(UUID uniqueId, UUID stayId, Instant issuedAt, String code){

        if(uniqueId == null) throw new InvalidEntryTicketException("uniqueId is null");
        if(stayId == null) throw new InvalidEntryTicketException("stayId is null");
        if(issuedAt == null) throw new InvalidEntryTicketException("issuedAt is null");
        if(code == null) throw new InvalidEntryTicketException("code is null");
   
    }

    static public EntryTicket create(UUID stayId,Instant issuedAt, String code){
        UUID uniqueId = UUID.randomUUID();
        validateData(uniqueId, stayId, issuedAt, code);
        return new EntryTicket(uniqueId, stayId, issuedAt, code);
    }

    static public EntryTicket recreate(UUID uniqueId, UUID stayId, Instant issuedAt, String code){
         validateData(uniqueId, stayId, issuedAt, code);
        return new EntryTicket(uniqueId, stayId , issuedAt, code);
    }


    // --- GETTERS Y SETTERS ---

    public UUID getUniqueId() {
        return uniqueId;
    }


    public UUID getStayId() {
        return stayId;
    }


    public Instant getIssuedAt() {
        return issuedAt;
    }

    public String getCode() {
        return code;
    }


}