package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.service.DetailExitMaterialService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detailExitMaterials")
public class DetailExitMaterialController {


    private final DetailExitMaterialService detailExitMaterialService;

    @Autowired
    public DetailExitMaterialController(DetailExitMaterialService detailExitMaterialService) {
        this.detailExitMaterialService = detailExitMaterialService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<DetailExitMaterial>>> getAllDetailExitMaterials() {
        List<DetailExitMaterial> detailExitMaterials = this.detailExitMaterialService.getAllDetailExitMaterials();
        if ( detailExitMaterials.isEmpty())  {
            ApiResponse<List<DetailExitMaterial>> response = new ApiResponse<>("No existen Salida de materiales", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<List<DetailExitMaterial>> response = new ApiResponse<>("Lista de Movimientos", detailExitMaterials);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DetailExitMaterial>> getDetailExitMaterialById(@PathVariable Long id) {
        DetailExitMaterial detailExitMaterial = this.detailExitMaterialService.getDetailExitMaterialById(id);
        if (detailExitMaterial == null) {
            ApiResponse<DetailExitMaterial> response = new ApiResponse<>("Salida de material no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<DetailExitMaterial> response = new ApiResponse<>("Salida de material encontrado", detailExitMaterial);
        return ResponseEntity.ok(response);
    }

    //TODO: Crear el DetailExitMaterial dentro del servicio de Movement cuando se cree una salida de material.
    // Evaluar type_returable si para usar el servicio de exitMatrial o entryMaterial (En MovementService)

    @PostMapping("/createDetailExitMaterial")
    public ResponseEntity<ApiResponse<DetailExitMaterial>> createDetailExitMaterial(@RequestBody DetailExitMaterial detailExitMaterial) {
        boolean savedDetailExitMaterial = this.detailExitMaterialService.createDetailExitMaterial(detailExitMaterial);
        ApiResponse<DetailExitMaterial> response;
        if (!savedDetailExitMaterial) {
            response = new ApiResponse<>("Salida de material NO creado", detailExitMaterial);
            return ResponseEntity.status(500).body(response);
        } else {
            response = new ApiResponse<>("Salida de material creado", detailExitMaterial);
            return ResponseEntity.status(201).body(response);
        }
    }

    @PatchMapping("/updateDetailExitMaterial/{id}")
    public ResponseEntity<ApiResponse<DetailExitMaterial>> updateDetailExitMaterial(@RequestBody DetailExitMaterial detailExitMaterial,@PathVariable Long id) {

        if (!this.detailExitMaterialService.existsDetailExitMaterialById(id)) {
            ApiResponse<DetailExitMaterial> response = new ApiResponse<>("Salida de material no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        boolean updatedDetailExitMaterial = this.detailExitMaterialService.updateDetailExitMaterial(detailExitMaterial, id);
        ApiResponse<DetailExitMaterial> response;
        if (!updatedDetailExitMaterial) {
            response = new ApiResponse<>("Salida de material NO actualizado", detailExitMaterial);
            return ResponseEntity.status(500).body(response);
        } else {
            DetailExitMaterial updatedDetailExitMaterialInDb = this.detailExitMaterialService.getDetailExitMaterialById(id);
            response = new ApiResponse<>("Salida de material actualizado", updatedDetailExitMaterialInDb);
            return ResponseEntity.status(200).body(response);
        }
    }

    @DeleteMapping("/deleteDetailExitMaterial/{id}")
    public ResponseEntity<ApiResponse<DetailExitMaterial>> deleteDetailExitMaterial(@PathVariable Long id) {
        if (!this.detailExitMaterialService.existsDetailExitMaterialById(id)) {
            ApiResponse<DetailExitMaterial> response = new ApiResponse<>("Salida de material no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }

        boolean deletedDetailExitMaterial = this.detailExitMaterialService.deleteDetailExitMaterial(id);
        ApiResponse<DetailExitMaterial> response;
        if (!deletedDetailExitMaterial) {
            response = new ApiResponse<>("Salida de material NO eliminado", null);
            return ResponseEntity.status(500).body(response);
        } else {
            response = new ApiResponse<>("Salida de material eliminado", null);
            return ResponseEntity.status(200).body(response);
        }
    }





}
