package com.enigmaOne.enigmaOne.service.dto;

import com.enigmaOne.enigmaOne.validation.groups.OnCreate;
import com.enigmaOne.enigmaOne.validation.groups.OnUpdate;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCreateUpdateDTO {


    @NotBlank(message = "El nombre es requerido.",groups =  {OnCreate.class,})
    @Size(min = 2 , message = "El nombre debe tener por lo menos un caracter", groups = {OnCreate.class, OnUpdate.class})
    private String firstName;

    @Size(min = 2 , message = "El apellido debe tener por lo menos un caracter", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "El apellido es requerido.",groups =  {OnCreate.class})
    private String lastName;

    @NotBlank(message = "El DNI es requerido.",groups = {OnCreate.class,})
    @Size(min = 8,max = 8,message = "El dni debe tener 8 caracteres",groups =  {OnCreate.class, OnUpdate.class })
    private String dni;

    @Size(min = 4 , message = "El Correo debe tener por lo menos 6 caracteres", groups = {OnCreate.class, OnUpdate.class})
    @NotBlank(message = "El correo es requerido.",groups =  {OnCreate.class,})
    @Email(message = "El formato del correo no es válido.",groups =  {OnCreate.class, })
    private String email;

    //Usar validation groups para volver optional esta opcion al momento de actualizar

    @NotBlank(message = "La contraseña no puede estar vacío.",groups = OnCreate.class)
    @Size(min = 6,message = "El mínimo de caracteres son 6",groups = {OnCreate.class, OnUpdate.class})
    private String password;

    private String address;
    private String cellphone;
    private String bankAccountNumber;
    private String bankAccountCciNumber;

    @NotNull(message = "El salario no puede ser vacío.",groups =  {OnCreate.class,})
    @Digits(integer = 8, fraction = 2, message = "El salario debe tener un formato numérico válido (ej. 1234.56)",groups = {OnCreate.class, OnUpdate.class })
    private Double salary;
    @Past(message = "La fecha de nacimiento debe ser en el pasado",groups =  {OnCreate.class, OnUpdate.class})
    private Date birthday;
    private String photo;

    private Boolean active;

    @NotNull(message = "El campo departamento no puede estar vacío.",groups =  {OnCreate.class, })
    private Long departmentId;
    @NotNull(message = "El campo del cargo del empleado no puede estar vacío.",groups =  {OnCreate.class, })
    private Long positionId;

}
