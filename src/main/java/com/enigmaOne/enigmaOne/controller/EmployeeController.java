package com.enigmaOne.enigmaOne.controller;


import com.enigmaOne.enigmaOne.service.EmployeeService;

import com.enigmaOne.enigmaOne.service.dto.ApiResponseTest;
import com.enigmaOne.enigmaOne.service.dto.EmployeeCreateUpdateDTO;
import com.enigmaOne.enigmaOne.service.dto.EmployeeResponseDTO;
import com.enigmaOne.enigmaOne.service.mapper.EmployeeMapper;
import com.enigmaOne.enigmaOne.validation.groups.OnCreate;
//import jakarta.validation.Valid;

import com.enigmaOne.enigmaOne.validation.groups.OnUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeMapper employeeMapper;
    private final EmployeeService employeeService;
    private Map<String,String> response = new HashMap<>();

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping()
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees() {
        return ResponseEntity.ok(this.employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id){
        if (this.employeeService.getEmployeeById(id) ==null){
            this.response.put("message", "No se encontró empleado con el ID: "+id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.response);
        }
        return  ResponseEntity.ok(this.employeeService.getEmployeeById(id));

    }

    @GetMapping("/searchForName/{name}")
    public ResponseEntity<ApiResponseTest<List<EmployeeResponseDTO>>> getEmployeeForByFirstName(@PathVariable String name){

     List<EmployeeResponseDTO> employee =  this.employeeService.getEmployeeByFirstName(name);
      if (employee!=null){
          ApiResponseTest<List<EmployeeResponseDTO>> response = new ApiResponseTest<>("Empleado encontrado",employee,200);
          return  ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
      } else {
          ApiResponseTest< List<EmployeeResponseDTO>> response = new ApiResponseTest<>("Empleado NO encontrado",employee,404);
          return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

    }

    @GetMapping("/searchForTerm/{term}")
    public ResponseEntity<ApiResponseTest<List<EmployeeResponseDTO>>> getEmployeeForTerm(@PathVariable String term){

        List<EmployeeResponseDTO> employee =  this.employeeService.findByAnyTerm(term);
        if (employee!=null){
            ApiResponseTest<List<EmployeeResponseDTO>> response = new ApiResponseTest<>("Empleado encontrado",employee,200);
            return  ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } else {
            ApiResponseTest< List<EmployeeResponseDTO>> response = new ApiResponseTest<>("Empleado NO encontrado",employee,404);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

    }


    @PostMapping(value = "/createEmployee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseTest<EmployeeResponseDTO>> addEmployee(
            @Validated(OnCreate.class) @RequestPart("employee") EmployeeCreateUpdateDTO employee,
            @RequestPart(value = "photo",required = false) MultipartFile photoFile){

        if (this.validToDatabaseDniAndEmail(employee)){
            ApiResponseTest<EmployeeResponseDTO> response = new ApiResponseTest<>("Empleado con DNI o Correo ya registrado", null,409);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if ( this.employeeService.saveEmployee(employee,photoFile)){

            ApiResponseTest<EmployeeResponseDTO> response = new ApiResponseTest<>("Empleado creado correctamente!", null,201);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else{
            ApiResponseTest<EmployeeResponseDTO> response = new ApiResponseTest<>("Ha ocurrido un error!", null,500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PatchMapping("/updateEmployee/{id}")
    public ResponseEntity<ApiResponseTest<EmployeeCreateUpdateDTO>> updateEmployee(
                                                                @Validated(OnUpdate.class)
                                                                @RequestPart("employee") EmployeeCreateUpdateDTO employee,
                                                                @RequestPart(value = "photo",required = false) MultipartFile photo,
                                                                @PathVariable Long id){

        if(!this.employeeService.existsEmployeeById(id)){
            ApiResponseTest<EmployeeCreateUpdateDTO> response = new ApiResponseTest<>("El empleado no existe con el id: " +id, null,404);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        if (this.validToDatabaseDniAndEmail(employee)){
            ApiResponseTest<EmployeeCreateUpdateDTO> response = new ApiResponseTest<>("Empleado con DNI o Correo ya registrado" , null,409);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        if (this.employeeService.updateEmployee(employee,photo,id)){
            ApiResponseTest<EmployeeCreateUpdateDTO> response = new ApiResponseTest<>("Empleado actualizado correctamente!" , employee,200);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }else {
            ApiResponseTest<EmployeeCreateUpdateDTO> response = new ApiResponseTest<>("Ha ocurrido un problema" , employee,500);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
     }

     @DeleteMapping("/deleteEmployee/{id}")
    public ResponseEntity<Map<String,String>> deleteEmployee(@PathVariable Long id){
        if(!this.employeeService.existsEmployeeById(id)){
            this.response.put("message","El empleado no existe: " +id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.response);
        }
            this.employeeService.deleteEmployee(id);
            this.response.put("message","Empleado eliminado correctamente");
            return ResponseEntity.ok(this.response);

    }


    private boolean validToDatabaseDniAndEmail(EmployeeCreateUpdateDTO employee){
        if (this.employeeService.existsEmployeeByDni(employee.getDni())){
           // this.response.put("message", "Employee con ese DNI: "+ employee.getDni() +" ya existe!");
           // return ResponseEntity.status(HttpStatus.CONFLICT).body(this.response);
            return true;
        }
        if (this.employeeService.existsEmployeeByEmail(employee.getEmail())){
           // this.response.put("message", "Employee con ese Email: " + employee.getEmail() +" ya existe!");
           // return ResponseEntity.status(HttpStatus.CONFLICT).body(this.response);
            return true;
        }
        return false;
    }


}
