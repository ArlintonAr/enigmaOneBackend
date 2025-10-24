package com.enigmaOne.enigmaOne.service.dto;

import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import com.enigmaOne.enigmaOne.persistence.types.MovementType;
import com.enigmaOne.enigmaOne.persistence.types.ReturnableType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementResponseDTO {


    private Long id;
    private String voucherNumber;
    private MovementType type;

    private String quantity;
    private ReturnableType returnable;
    private String transactionCode;

    private String materialRequesterFirstName;
    private String materialRequesterLastName;

    private Date returnDate;
    //Relaciones
    private String employeeFirstName; //
    private String employeeLastName;

    private List<DetailEntryMaterial> detailEntryMaterials;
    private List<DetailExitMaterial> detailExitMaterials;

    //Stock
    private Long stockId;

    private Date created_at;
    private Date updated_at;

}
