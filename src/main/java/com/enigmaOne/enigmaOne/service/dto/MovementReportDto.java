package com.enigmaOne.enigmaOne.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementReportDto {
    private Long id;
    private String transactionCode;
    private Date returnDate;
    private String authorizer;
    private String requester;
    private Integer quantity;
    private String createdAt;
    private List<Map<String,Object>> materials;
}
