package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.service.MovementService;
import com.enigmaOne.enigmaOne.service.JasperReportService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import com.enigmaOne.enigmaOne.service.dto.ApiResponseTest;
import com.enigmaOne.enigmaOne.service.dto.MovementResponseDTO;
import com.enigmaOne.enigmaOne.service.dto.MovementCreateDTO;
import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/movements")
public class MovementController {

    private final MovementService movementService;
    private final JasperReportService jasperReportService;

    public MovementController(MovementService movementService, JasperReportService jasperReportService){
        this.movementService = movementService;
        this.jasperReportService = jasperReportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseTest<List<MovementResponseDTO>>> getMovementAll() {
        List<MovementResponseDTO> movements = this.movementService.getAllMovements();
       if ( movements.isEmpty())  {
            ApiResponseTest<List<MovementResponseDTO>> response = new ApiResponseTest<>("No existen Movimientos", null,404);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponseTest<List<MovementResponseDTO>> response = new ApiResponseTest<>("Lista de Movimientos", movements,200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/findMovementForRequerter/{firstName}")
    public ResponseEntity<ApiResponseTest<List<MovementResponseDTO>>> getMovementByMaterialRequesterFirstName(@PathVariable String firstName) {
         List<MovementResponseDTO> movements = this.movementService.getMovementsByMaterialRequesterFirstName(firstName);
          if (movements.isEmpty()) {
                ApiResponseTest<List<MovementResponseDTO>> response = new ApiResponseTest<>("Movimientos no encontrados", null,404);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
          }else{
                ApiResponseTest<List<MovementResponseDTO>> response = new ApiResponseTest<>("Movimientos encontrados", movements,200);
                return ResponseEntity.status(200).body(response);
          }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Movement>> getMovementById(@PathVariable Long id) {
        Movement movement = this.movementService.getMovementById(id);
        if (movement == null) {
            ApiResponse<Movement> response = new ApiResponse<>("Movimiento no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<Movement> response = new ApiResponse<>("Movimiento encontrado", movement);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/searchForTransactionCode/{transactionCode}")
    public ResponseEntity<ApiResponse<List<MovementResponseDTO>>> getMovementByTransactionCode(@PathVariable String transactionCode) {
        List<MovementResponseDTO> movements = this.movementService.getMovementByTransactionCode(transactionCode);
        if (movements == null) {
            ApiResponse< List<MovementResponseDTO>> response = new ApiResponse<>("Movimiento no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<List<MovementResponseDTO>> response = new ApiResponse<>("Movimiento encontrado", movements);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/searchForEmployeeId/{employeeId}")
    public ResponseEntity<ApiResponseTest<List<MovementResponseDTO>>> getMovementByEmployeeId(@PathVariable Long employeeId) {

       List<MovementResponseDTO> movements = this.movementService.getMovementsByEmployeeId(employeeId);
        if (movements.isEmpty()) {
            ApiResponseTest<List<MovementResponseDTO>> response = new ApiResponseTest<>("Movimientos no encontrados", null,404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }else{
            ApiResponseTest<List<MovementResponseDTO>> response = new ApiResponseTest<>("Movimientos encontrados", movements,200);
            return ResponseEntity.status(200).body(response);
        }

    }



    @PostMapping("/createMovement")
    public ResponseEntity<ApiResponse<MovementResponseDTO>> createMovement(@RequestBody MovementCreateDTO dto) {
        // Mapear DTO a entidad Movement
        Movement movement = new Movement();
        movement.setType(dto.getType());
        movement.setReturnable(dto.getReturnable());
        movement.setMaterialRequesterId(dto.getMaterialRequesterId());
        if (dto.getReturnDate() != null) {
            // Asignar la fecha tal cual viene en el DTO sin normalizar por zonas horarias
            movement.setReturnDate(dto.getReturnDate());
        }

        // Mapear detalles de salida
        if (dto.getDetailExitMaterials() != null && !dto.getDetailExitMaterials().isEmpty()){
            List<DetailExitMaterial> exitDetails = dto.getDetailExitMaterials().stream().map(d -> {
                DetailExitMaterial det = new DetailExitMaterial();
                det.setStockId(d.getStockId());
                det.setQuantity(d.getQuantity());
                det.setDestinationMaterial(d.getDestinationMaterial());
                return det;
            }).collect(Collectors.toList());
            movement.setDetailExitMaterials(exitDetails);
        }

        // Mapear detalles de entrada
        if (dto.getDetailEntryMaterials() != null && !dto.getDetailEntryMaterials().isEmpty()){
            List<DetailEntryMaterial> entryDetails = dto.getDetailEntryMaterials().stream().map(d -> {
                DetailEntryMaterial det = new DetailEntryMaterial();
                det.setStockId(d.getStockId());
                det.setQuantity(d.getQuantity());
                det.setDestinationMaterial(d.getDestinationMaterial());
                return det;
            }).collect(Collectors.toList());
            movement.setDetailEntryMaterials(entryDetails);
        }

        boolean savedMovement = this.movementService.createMovement(movement);
        if (!savedMovement) {
            ApiResponse<MovementResponseDTO> response = new ApiResponse<>("Movimiento NO creado", null);
            return ResponseEntity.status(500).body(response);
        } else {
            // Obtener el DTO mapeado y devolverlo (consistentemente con otros endpoints como Employee)
            List<MovementResponseDTO> dtoList = this.movementService.getMovementByTransactionCode(movement.getTransactionCode());
            MovementResponseDTO responseDto = (dtoList != null && !dtoList.isEmpty()) ? dtoList.get(0) : null;
            ApiResponse<MovementResponseDTO> response = new ApiResponse<>("Movimiento creado", responseDto);
            return ResponseEntity.status(201).body(response);
         }
    }

    @PatchMapping("/updateMovement/{id}")
    public ResponseEntity<ApiResponse<Movement>> updateMovement(@RequestBody Movement movement, @PathVariable Long id) {

        if (!this.movementService.existsMovementById(id)) {
            ApiResponse<Movement> response = new ApiResponse<>("Movimiento no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }

        boolean updatedMovement = this.movementService.updateMovement(id, movement);
        ApiResponse<Movement> response;
        if (!updatedMovement) {
            response = new ApiResponse<>("Movimiento NO actualizado", movement);
            return ResponseEntity.status(500).body(response);
        } else {
            Movement updatedMovementData = this.movementService.getMovementById(id);
            response = new ApiResponse<>("Movimiento actualizado", updatedMovementData);
            return ResponseEntity.status(200).body(response);
        }


    }

    @DeleteMapping("/deleteMovement/{id}")
    public ResponseEntity<ApiResponse<Movement>> deleteMovement(@PathVariable Long id) {
        if (!this.movementService.existsMovementById(id) ) {
            ApiResponse<Movement> response = new ApiResponse<>("Movimiento no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<Movement> response;
        if (!this.movementService.deleteMovement(id)) {
                response = new ApiResponse<>("Movimiento NO eliminado", null);
                return ResponseEntity.status(500).body(response);
            } else {
                response = new ApiResponse<>("Movimiento eliminado", null);
                return ResponseEntity.status(200).body(response);
            }
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> getMovementReport(@PathVariable("id") Long id) {
        byte[] pdf = jasperReportService.generateMovementReportPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "movement-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

}
