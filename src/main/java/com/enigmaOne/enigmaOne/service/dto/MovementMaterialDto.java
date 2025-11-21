package com.enigmaOne.enigmaOne.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementMaterialDto {
    private Long id;
    private Long stockId;
    private String stockCode;
    private Integer quantity;
    private String destinationMaterial;
    private String type; // ENTRY or EXIT
}
