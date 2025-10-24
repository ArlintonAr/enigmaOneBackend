package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;
import com.enigmaOne.enigmaOne.service.WarehouseService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouses")
public class WarehouseController {


    private WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;

    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Warehouse>>> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();

       if (warehouses.isEmpty() ){
        ApiResponse<List <Warehouse>> response = new ApiResponse<>("No hay almacenes disponibles", warehouses);
        return ResponseEntity.status(404).body(response);
       }else {
              ApiResponse<List<Warehouse>> response = new ApiResponse<>("Lista de almacenes", warehouses);
              return ResponseEntity.ok(response);
       }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> getWarehouseById(@PathVariable  Long id) {
        Warehouse warehouse = warehouseService.getWarehouseById(id);
        if (warehouse == null) {
            ApiResponse<Warehouse> response = new ApiResponse<>("Almacén no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        ApiResponse<Warehouse> response = new ApiResponse<>("Almacén encontrado", warehouse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createWarehouse")
    public ResponseEntity<ApiResponse<Warehouse>> createWarehouse(@RequestBody Warehouse warehouse) {

        boolean savedWarehouse = warehouseService.createWarehouse(warehouse);
        ApiResponse<Warehouse> response;
        if (!savedWarehouse) {
            response = new ApiResponse<>("Almacén NO creado", warehouse);
            return ResponseEntity.status(500).body(response);
        } else {
            Warehouse createdWarehouse = warehouseService.getWarehouseById(warehouse.getId());
            response = new ApiResponse<>("Almacén creado", createdWarehouse);
            return ResponseEntity.status(201).body(response);
        }
    }

    @PatchMapping("/updateWarehouse/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> updateWarehouse(@RequestBody Warehouse warehouse, @PathVariable Long id) {
        if (!this.warehouseService.existsWarehouseById(id)){
            ApiResponse<Warehouse> response = new ApiResponse<>("El almacén no existe", null);
            return ResponseEntity.status(404).body(response);
        }
        boolean updatedWarehouse = warehouseService.updateWarehouse( id,warehouse);
        ApiResponse<Warehouse> response;
        if (!updatedWarehouse) {
            response = new ApiResponse<>("Almacén NO actualizado", warehouse);
            return ResponseEntity.status(500).body(response);
        } else {
            Warehouse updated = warehouseService.getWarehouseById(id);
            response = new ApiResponse<>("Almacén actualizado", updated);
            return ResponseEntity.status(200).body(response);
        }
    }

    @DeleteMapping("/deleteWarehouse/{id}")
    public ResponseEntity<ApiResponse<Warehouse>> deleteWarehouse(@PathVariable Long id) {
        if (!this.warehouseService.existsWarehouseById(id)){
            ApiResponse<Warehouse> response = new ApiResponse<>("El almacén no existe", null);
            return ResponseEntity.status(404).body(response);
        }
        boolean deletedWarehouse = warehouseService.deleteWarehouse(id);
        ApiResponse<Warehouse> response;
        if (!deletedWarehouse) {
            response = new ApiResponse<>("Almacén NO eliminado", null);
            return ResponseEntity.status(500).body(response);
        } else {
            response = new ApiResponse<>("Almacén eliminado", null);
            return ResponseEntity.status(200).body(response);
        }
    }

}
