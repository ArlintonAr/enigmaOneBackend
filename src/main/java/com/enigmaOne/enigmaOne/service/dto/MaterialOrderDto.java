package com.enigmaOne.enigmaOne.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialOrderDto {
    private Long id;
    private String code;
    private Integer quantity;
    private String unitOfMeasure;
    private String characteristics;
    private String photo;
    private String estimatedDateStock;
    private String observations; // agregado para mostrar observaciones en el subreporte
}
