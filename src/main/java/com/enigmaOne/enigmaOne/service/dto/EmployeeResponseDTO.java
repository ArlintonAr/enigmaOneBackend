package com.enigmaOne.enigmaOne.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String dni;
    private String email;

    private String address;
    private String cellphone;
    private String bankAccountNumber;
    private String bankAccountCciNumber;
    private Double salary;
    private Date birthday;
    private String photo;
    private boolean active;

    private String departmentName;
    private String positionName;

}
