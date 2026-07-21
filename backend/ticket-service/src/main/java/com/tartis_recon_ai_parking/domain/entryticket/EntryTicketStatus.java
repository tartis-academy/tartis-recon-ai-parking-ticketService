package com.tartis_recon_ai_parking.domain.entryticket;

public enum EntryTicketStatus {
    ISSUED,   // emitido en el check-in
    USED,     // consumido en el check-out (IN-21: no reutilizable)
    LOST;     // extraviado -> tarifa fija de penalizacion (IN-22)

    public boolean isTerminal() {
        return this != ISSUED;
    }
}
