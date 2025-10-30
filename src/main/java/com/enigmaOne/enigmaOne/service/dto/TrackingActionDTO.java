package com.enigmaOne.enigmaOne.service.dto;

import lombok.Data;

@Data
public class TrackingActionDTO {
    private String state; // PEDIDO, APROBADO, RUTA, ALMACEN, RECHAZADO
    private String note;
}

