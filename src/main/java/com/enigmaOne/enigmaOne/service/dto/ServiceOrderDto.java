package com.enigmaOne.enigmaOne.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderDto {
    private Long id;
    private String code;
    private String characteristics;
    private String deliveryDate;
}

