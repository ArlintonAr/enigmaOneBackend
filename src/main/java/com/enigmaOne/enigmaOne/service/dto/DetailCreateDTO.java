package com.enigmaOne.enigmaOne.service.dto;

public class DetailCreateDTO {
    private Long stockId;
    private Integer quantity;
    private String destinationMaterial;

    public DetailCreateDTO() {}

    public Long getStockId() { return stockId; }
    public void setStockId(Long stockId) { this.stockId = stockId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getDestinationMaterial() { return destinationMaterial; }
    public void setDestinationMaterial(String destinationMaterial) { this.destinationMaterial = destinationMaterial; }
}

