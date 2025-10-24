package com.enigmaOne.enigmaOne.service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

    //TODO: Validar lo que el usuario va a ingresar
    @Size(min = 4 , message = "El Correo debe tener por lo menos 6 caracteres")
    @NotBlank(message = "El correo es requerido.")
    @Email(message = "El formato del correo no es válido.")
    private String email;
    @Size(min = 6,message = "El mínimo de caracteres son 6")
    @NotBlank(message = "La contraseña no puede estar vacío.")
    private String password;

}
