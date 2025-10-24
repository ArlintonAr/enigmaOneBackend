package com.enigmaOne.enigmaOne.service.dto;

import com.enigmaOne.enigmaOne.persistence.types.MovementType;
import com.enigmaOne.enigmaOne.persistence.types.ReturnableType;

import java.util.Date;
import java.util.List;

public class MovementCreateDTO {
    private MovementType type;
    private ReturnableType returnable;
    private Long materialRequesterId;
    private Date returnDate;
    private List<DetailCreateDTO> detailExitMaterials;
    private List<DetailCreateDTO> detailEntryMaterials;

    public MovementCreateDTO() {}

    public MovementType getType() { return type; }
    public void setType(MovementType type) { this.type = type; }

    public ReturnableType getReturnable() { return returnable; }
    public void setReturnable(ReturnableType returnable) { this.returnable = returnable; }

    public Long getMaterialRequesterId() { return materialRequesterId; }
    public void setMaterialRequesterId(Long materialRequesterId) { this.materialRequesterId = materialRequesterId; }

    public Date getReturnDate() { return returnDate; }
    public void setReturnDate(Date returnDate) { this.returnDate = returnDate; }

    public List<DetailCreateDTO> getDetailExitMaterials() { return detailExitMaterials; }
    public void setDetailExitMaterials(List<DetailCreateDTO> detailExitMaterials) { this.detailExitMaterials = detailExitMaterials; }

    public List<DetailCreateDTO> getDetailEntryMaterials() { return detailEntryMaterials; }
    public void setDetailEntryMaterials(List<DetailCreateDTO> detailEntryMaterials) { this.detailEntryMaterials = detailEntryMaterials; }
}

