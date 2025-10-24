package com.enigmaOne.enigmaOne.controller;


import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import com.enigmaOne.enigmaOne.service.DetailEntryMaterialService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detailEntryMaterials")
public class DetailEntryMaterialController {

    private final DetailEntryMaterialService detailEntryMaterialService;

    @Autowired
    public DetailEntryMaterialController(DetailEntryMaterialService detailEntryMaterialService) {
        this.detailEntryMaterialService = detailEntryMaterialService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<DetailEntryMaterial>>> getAllDetailEntryMaterials() {
        List<DetailEntryMaterial> detailEntryMaterials = this.detailEntryMaterialService.getAllDetailEntryMaterials();
        if ( detailEntryMaterials.isEmpty())  {
            ApiResponse<List<DetailEntryMaterial>> response = new ApiResponse<>("No existen Entrada de materiales", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<List<DetailEntryMaterial>> response = new ApiResponse<>("Lista de Entrada de materiales", detailEntryMaterials);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DetailEntryMaterial>> getDetailEntryMaterialById(@PathVariable Long id) {
        DetailEntryMaterial detailEntryMaterial = this.detailEntryMaterialService.getDetailEntryMaterialById(id);
        if (detailEntryMaterial == null) {
            ApiResponse<DetailEntryMaterial> response = new ApiResponse<>("Entrada de material no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<DetailEntryMaterial> response = new ApiResponse<>("Entrada de material encontrado", detailEntryMaterial);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createDetailEntryMaterial")
    public ResponseEntity<ApiResponse<DetailEntryMaterial>> createDetailEntryMaterial(@RequestBody DetailEntryMaterial detailEntryMaterial) {
        boolean savedDetailEntryMaterial = this.detailEntryMaterialService.createDetailEntryMaterial(detailEntryMaterial);
        ApiResponse<DetailEntryMaterial> response;
        if (!savedDetailEntryMaterial) {
            response = new ApiResponse<>("Entrada de material NO creado", detailEntryMaterial);
            return ResponseEntity.status(500).body(response);
        } else {
            response = new ApiResponse<>("Entrada de material creado", detailEntryMaterial);
            return ResponseEntity.status(201).body(response);
        }
    }

    @PatchMapping("/updateDetailEntryMaterial/{id}")
    public ResponseEntity<ApiResponse<DetailEntryMaterial>> updateDetailEntryMaterial(@RequestBody DetailEntryMaterial detailEntryMaterial, @PathVariable Long id) {

        if (!this.detailEntryMaterialService.existsDetailEntryMaterialById(id)){
            ApiResponse<DetailEntryMaterial> response = new ApiResponse<>("Entrada de material no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }

        boolean updatedDetailEntryMaterial = this.detailEntryMaterialService.updateDetailEntryMaterial(detailEntryMaterial, id);
        ApiResponse<DetailEntryMaterial> response;
        if (!updatedDetailEntryMaterial) {
            response = new ApiResponse<>("Entrada de material NO actualizado", detailEntryMaterial);
            return ResponseEntity.status(500).body(response);
        } else {
            DetailEntryMaterial updatedDetailEntryMaterialInDB = this.detailEntryMaterialService.getDetailEntryMaterialById(id);
            response = new ApiResponse<>("Entrada de material actualizado", updatedDetailEntryMaterialInDB);
            return ResponseEntity.status(200).body(response);
        }
    }

    @DeleteMapping("/deleteDetailEntryMaterial/{id}")
    public ResponseEntity<ApiResponse<DetailEntryMaterial>> deleteDetailEntryMaterial(@PathVariable Long id) {
        if (!this.detailEntryMaterialService.existsDetailEntryMaterialById(id)) {
            ApiResponse<DetailEntryMaterial> response = new ApiResponse<>("Entrada de material no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        boolean deletedDetailEntryMaterial = this.detailEntryMaterialService.deleteDetailEntryMaterial(id);
        ApiResponse<DetailEntryMaterial> response;
        if (!deletedDetailEntryMaterial) {
            response = new ApiResponse<>("Entrada de material NO eliminado", null);
            return ResponseEntity.status(500).body(response);
        } else {
            response = new ApiResponse<>("Entrada de material eliminado", null);
            return ResponseEntity.status(200).body(response);
        }
    }

}
