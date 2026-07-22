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
    private EntryTicketStatus status;

    // --- CONSTRUCTORES ---


    // Constructor con todos los parámetros
    private EntryTicket(UUID uniqueId, UUID stayId, Instant issuedAt, String code, EntryTicketStatus status) {

        this.uniqueId = uniqueId;
        this.stayId = stayId;
        this.issuedAt = issuedAt;
        this.code = code;
        this.status = status;
    }

    static private void validateData(UUID uniqueId, UUID stayId, Instant issuedAt, String code, EntryTicketStatus status){

        if(uniqueId == null) throw new InvalidEntryTicketException("uniqueId is null");
        if(stayId == null) throw new InvalidEntryTicketException("stayId is null");
        if(issuedAt == null) throw new InvalidEntryTicketException("issuedAt is null");
        if(code == null) throw new InvalidEntryTicketException("code is null");
        if(status == null) throw new InvalidEntryTicketException("status is null");
   
    }

    static public EntryTicket create(UUID stayId,Instant issuedAt, String code, EntryTicketStatus status){
        UUID uniqueId = UUID.randomUUID();
        validateData(uniqueId, stayId, issuedAt, code, status);
        return new EntryTicket(uniqueId, stayId, issuedAt, code, status);
    }

    static public EntryTicket recreate(UUID uniqueId, UUID stayId, Instant issuedAt, String code, EntryTicketStatus status){
        validateData(uniqueId, stayId, issuedAt, code, status);
        return new EntryTicket(uniqueId, stayId , issuedAt, code, status);
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
    
    public EntryTicketStatus getStatus(){
        return status;
    }

}
