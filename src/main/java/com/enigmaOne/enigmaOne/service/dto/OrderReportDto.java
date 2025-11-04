package com.enigmaOne.enigmaOne.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportDto {
    private Long id;
    private String type;
    private String employeeName;
    private String departmentName; // agregado
    private String approvalStatus;
    private String currentTrackingState;
    private String estimatedDateStock;
    private String createdAt; // fecha de creación/pedido formateada
    private List<MaterialOrderDto> materialOrders;
    private List<ServiceOrderDto> serviceOrders;
}
