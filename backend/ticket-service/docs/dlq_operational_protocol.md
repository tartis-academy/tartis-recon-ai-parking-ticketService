# Protocolo Operacional de Dead Letter Queue (DLQ) — `ticket-service`

Este documento establece el procedimiento operativo, responsabilidades e impacto funcional cuando un mensaje acaba en la **Dead Letter Queue (DLQ)** de `ticket-service`.

---

## 1. Contexto e Impacto de Negocio

* **Cola Principal:** `ticket-service-stay-closed-queue`
* **Dead Letter Exchange (DLX):** `ticket-service-stay-closed-dlx`
* **Dead Letter Queue (DLQ):** `ticket-service-stay-closed-dlq`
* **Evento procesado:** `StayClosedEvent` (Cierre de estancia de un vehículo).

### 🚨 Impacto de un mensaje en la DLQ:
Un mensaje en la DLQ de `ticket-service` representa un **recibo/ticket de cobro que no ha sido emitido ni guardado en la base de datos**.
- **Riesgo:** Pérdida de registro contable / facturación de la estancia finalizada.
- **Causa típica:** Fallos de datos no recuperables (ej. inconsistencia grave) o caída persistente de la infraestructura tras agotar los 3 reintentos automáticos con backoff exponencial.

---

## 2. Roles y Responsabilidades

* **Responsable de la revisión:** Equipo de Operaciones / Soporte L2 / Administrador del Sistema.
* **Mecanismo de Monitoreo:** 
  - Alerta configurada en Grafana / Prometheus o consola de RabbitMQ cuando la métrica `rabbitmq_queue_messages{queue="ticket-service-stay-closed-dlq"}` sea `> 0`.

---

## 3. Protocolo de Diagnóstico y Resolución

Cuando un mensaje llega a la DLQ, el operador **NO** puede dejar el mensaje sin procesar. Se debe seguir este flujo:

```mermaid
graph TD
    A[Alerta: Mensaje en ticket-service-stay-closed-dlq] --> B[Acceder a RabbitMQ Management UI]
    B --> C[Inspeccionar cabeceras x-first-death-reason y stacktrace]
    C --> D{¿Fallo temporal de infraestructura?}
    D -- Sí --> E[Resolver problema de BD/Red + Re-queue a la cola principal]
    D -- No --> F[Fallo de datos corruptos / negocio]
    F --> G[Generar ticket manualmente / Auditoría en BD + Purga de DLQ]
```

### Paso 1: Inspección del Mensaje
1. Acceder a la interfaz de RabbitMQ Management UI (`http://localhost:15672` o consola de staging/producción).
2. Seleccionar la cola `ticket-service-stay-closed-dlq`.
3. Hacer clic en **"Get Message(s)"** para ver el contenido del mensaje y sus cabeceras.
4. Revisar la cabecera `x-first-death-reason` y `x-exception-stacktrace` para identificar la causa raíz.

### Paso 2: Acción Correctora

#### Opción A: Fallo de Infraestructura Temporal (ej. BD fuera de servicio brevemente)
Si la causa fue una caída de base de datos que ya ha sido resuelta:
1. Usar la funcionalidad de **Move Messages** (o reenviar mediante la UI/script) desde `ticket-service-stay-closed-dlq` hacia la cola principal `ticket-service-stay-closed-queue`.
2. Verificar en los logs de `ticket-service` que el mensaje se ha procesado con éxito.

#### Opción B: Fallo por Datos Inconsistentes o Corruptos
Si el mensaje no se puede procesar automáticamente debido a datos corruptos:
1. Extraer los datos del evento (`stayId`, `exitDate`, `totalAmount`).
2. Insertar/regularizar el ticket manualmente mediante la API administrativa o script SQL de soporte.
3. Registrar la incidencia en la bitácora de auditoría de operaciones.
4. Eliminar/purgar el mensaje corregido de la DLQ.
