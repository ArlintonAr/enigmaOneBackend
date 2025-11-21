package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;
import com.enigmaOne.enigmaOne.service.WarehouseService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import com.enigmaOne.enigmaOne.service.dto.ApiResponseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseTest<List<Warehouse>>> getAllWarehouses(){
        List<Warehouse> warehouses = this.warehouseService.getAllWarehouses();
        if (warehouses.isEmpty()){
            ApiResponseTest<List<Warehouse>> response = new ApiResponseTest<>("Warehouse vacío", warehouses,404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponseTest<List<Warehouse>> response = new ApiResponseTest<>("Lista de Warehouses", warehouses,200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseTest<Warehouse>> getWarehouseById(@PathVariable Long id){
        Warehouse warehouse = this.warehouseService.getWarehouseById(id);
        if (warehouse == null){
            ApiResponseTest<Warehouse> response = new ApiResponseTest<>("Warehouse no encontrado", null,404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponseTest<Warehouse> response = new ApiResponseTest<>("Warehouse encontrado", warehouse,200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createWarehouse")
    public ResponseEntity<ApiResponseTest<Warehouse>> createWarehouse(@RequestBody Warehouse warehouse){
        boolean created = this.warehouseService.createWarehouse(warehouse);
        if (created){
            ApiResponseTest<Warehouse> response = new ApiResponseTest<>("Warehouse creado", warehouse,201);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        ApiResponseTest<Warehouse> response = new ApiResponseTest<>("Error al crear Warehouse", null,500);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PatchMapping("/updateWarehouse/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> updateWarehouse(@PathVariable Long id, @RequestBody Warehouse warehouse){
        if (!this.warehouseService.existsWarehouseById(id)){
            ApiResponse<Warehouse> response = new ApiResponse<>("Warehouse no encontrado", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        boolean updated = this.warehouseService.updateWarehouse(id, warehouse);
        if (updated){
            ApiResponse<Warehouse> response = new ApiResponse<>("Warehouse actualizado", this.warehouseService.getWarehouseById(id));
            return ResponseEntity.ok(response);
        }
        ApiResponse<Warehouse> response = new ApiResponse<>("Error al actualizar Warehouse", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @DeleteMapping("/deleteWarehouse/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> deleteWarehouse(@PathVariable Long id){
        if (!this.warehouseService.existsWarehouseById(id)){
            ApiResponse<Warehouse> response = new ApiResponse<>("Warehouse no encontrado", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        boolean deleted = this.warehouseService.deleteWarehouse(id);
        if (deleted){
            ApiResponse<Warehouse> response = new ApiResponse<>("Warehouse eliminado", null);
            return ResponseEntity.ok(response);
        }
        ApiResponse<Warehouse> response = new ApiResponse<>("Error al eliminar Warehouse", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
