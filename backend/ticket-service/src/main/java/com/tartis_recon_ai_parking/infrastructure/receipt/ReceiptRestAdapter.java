package com.tartis_recon_ai_parking.infrastructure.receipt.adapter.input.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/receipts")
public class ReceiptRestAdapter {

    // TODO: inyectar los casos de uso (CreateReceiptUseCase, GetReceiptUseCase...)
    // y el mapper, siguiendo el patron de VehicleRestAdapter.

    /**
     * Genera el recibo de salida/pago para una estancia finalizada.
     * POST /v1/receipts
     */
    @PostMapping
    public ResponseEntity<Void> createReceipt(@RequestBody Object request) {
        // TODO: implementar creacion de receipt
        return null;
    }

    /**
     * Lista los recibos, opcionalmente filtrados por stayId.
     * GET /v1/receipts
     */
    @GetMapping
    public ResponseEntity<Void> listReceipts(@RequestParam(required = false) UUID stayId) {
        // TODO: implementar listado de receipts
        return null;
    }

    /**
     * Recupera un recibo por su ID.
     * GET /v1/receipts/{receiptId}
     */
    @GetMapping("/{receiptId}")
    public ResponseEntity<Void> getReceiptById(@PathVariable UUID receiptId) {
        // TODO: implementar consulta de receipt por id
        return null;
    }

    /**
     * Marca un recibo como PERDIDO (IN-22): aplica tarifa de penalizacion
     * en vez de la tarifa normal.
     * PATCH /v1/receipts/{receiptId}/lost
     */
    @PatchMapping("/{receiptId}/lost")
    public ResponseEntity<Void> markReceiptLost(@PathVariable UUID receiptId) {
        // TODO: implementar marcado de receipt como perdido
        return null;
    }
}