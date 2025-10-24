package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.Config.CustomUserDetails;
import com.enigmaOne.enigmaOne.Config.JwtUtil;
import com.enigmaOne.enigmaOne.service.EmployeeService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import com.enigmaOne.enigmaOne.service.dto.EmployeeResponseDTO;
import com.enigmaOne.enigmaOne.service.dto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;


@RestController
@RequestMapping("/authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private EmployeeService employeeService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, EmployeeService employeeService) {
            this.authenticationManager = authenticationManager;
            this.jwtUtil = jwtUtil;
            this.employeeService = employeeService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Validated @RequestBody LoginDto loginDto){
        try {
            UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword());
            Authentication  authentication = this.authenticationManager.authenticate(login);

            if(!authentication.isAuthenticated()){
                return ResponseEntity.status(401).body(new ApiResponse<>("Usuario o contraseña incorrectos",null));
            }

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            String lastName  =userDetails.getLastName();
            String firstName = userDetails.getFirstName();
            String email = userDetails.getUsername();
            String photo = userDetails.getPhoto();
            Long id = userDetails.getId();
            String jwt = this.jwtUtil.generateJWT(loginDto.getEmail());


            // Usar HashMap en lugar de Map.of para evitar NullPointerException si algún valor es null
            Map<String,String> responseBody = new HashMap<>();
            responseBody.put("token", jwt != null ? jwt : "");
            responseBody.put("firstName", firstName != null ? firstName : "");
            responseBody.put("lastName", lastName != null ? lastName : "");
            responseBody.put("email", email != null ? email : "");
            responseBody.put("photo", photo != null ? photo : "");
            responseBody.put("id", String.valueOf(id));
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION,jwt).body(new ApiResponse<>("Autenticación exitosa",responseBody));
        } catch (Exception e) {
            // Imprimir stacktrace para diagnóstico. Mantener respuesta genérica al cliente.
            e.printStackTrace();
            return ResponseEntity.status(401).body(new ApiResponse<>("Ha ocurrido un problema en la verificación de credenciales.",null));
        }
    }

    @GetMapping("/check-status")
    public ResponseEntity<ApiResponse<?>> checkStatus(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated()){
            return ResponseEntity.status(401).body(new ApiResponse<>("No autenticado",null));
        }

        Long idUser = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            idUser = ((CustomUserDetails) principal).getId();
        } else if (principal != null && !"anonymousUser".equals(principal.toString())) {
            try {
                idUser = Long.parseLong(principal.toString());
            } catch (NumberFormatException ex) {
                idUser = null;
            }
        }

        EmployeeResponseDTO user = null;
        if (idUser != null) {
            user = this.employeeService.getEmployeeById(idUser);
        }

        String lastName = user != null ? user.getLastName() : "";
        String firstName = user != null ? user.getFirstName() : "";
        String email = user != null ? user.getEmail() : "";
        String photo = user != null ? user.getPhoto() : "";
        Long id = user != null ? user.getId() : idUser;

        String token = this.jwtUtil.generateJWT(email);
        Map<String,String> responseBody =  new HashMap<>();
        responseBody.put("firstName", firstName != null ? firstName : "");
        responseBody.put("lastName", lastName != null ? lastName : "");
        responseBody.put("email", email != null ? email : "");
        responseBody.put("id", id != null ? String.valueOf(id) : "");
        responseBody.put("token", token != null ? token : "");
        responseBody.put("photo", photo != null ? photo : "");

        return ResponseEntity.ok().body(new ApiResponse<>("Usuario autenticado",responseBody));
    }

}
