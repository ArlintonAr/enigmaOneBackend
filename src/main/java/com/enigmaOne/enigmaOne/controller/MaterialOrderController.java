package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;

import com.enigmaOne.enigmaOne.service.MaterialOrderService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import com.enigmaOne.enigmaOne.service.dto.ApiResponseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(("/materialOrders"))
public class MaterialOrderController {


    private MaterialOrderService materialOrderService;

    @Autowired
    public MaterialOrderController(MaterialOrderService materialOrderService){
        this.materialOrderService = materialOrderService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<MaterialOrder>>> getAllMaterialOrder(){
        List<MaterialOrder> materialOrders = this.materialOrderService.getAllMaterialOrder();

        if(materialOrders.isEmpty()){
            ApiResponse<List<MaterialOrder>> response = new ApiResponse<>("Ordenes de Materiales vacío",null);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<List<MaterialOrder>> response = new ApiResponse<>("Lista de Ordenes",materialOrders);
        return ResponseEntity.ok( response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialOrder>> getMaterialOrderById(@PathVariable Long id){
        MaterialOrder materialOrder= this.materialOrderService.findById(id);
        if (materialOrder==null){

            ApiResponse<MaterialOrder> response = new ApiResponse<>("Orden de material NO encontrada",null);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<MaterialOrder> response = new ApiResponse<>("Orden encontrada",materialOrder);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createMaterialOrder")
    public ResponseEntity<ApiResponseTest<MaterialOrder>> createMaterialOrder(
            @RequestPart("materialOrder") MaterialOrder materialOrder,
            @RequestPart(value = "photo",required = false) MultipartFile photoFile)
    {
         boolean savedMaterialOrder = this.materialOrderService.saveMaterialOrder(materialOrder,photoFile);
        ApiResponseTest<MaterialOrder> response;
        if (!savedMaterialOrder){
            response = new ApiResponseTest<>("Orden de material NO creada", materialOrder,204);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }else{
            response = new ApiResponseTest<>("Orden de material Creada", materialOrder,200);
            return ResponseEntity.ok(response);
        }

    }

    //Actualizar
    @PatchMapping("/updateMaterialOrder/{id}")
    public ResponseEntity<ApiResponse<MaterialOrder>> updateMaterialOrder(
            @PathVariable Long id,
            @RequestPart("materialOrder") MaterialOrder materialOrder,
            @RequestPart(value = "photo",required = false) MultipartFile photoFile
    ){
        ApiResponse<MaterialOrder> response;
        if (!this.materialOrderService.existMaterialOrderById(id)){
            response = new ApiResponse<>("Orden de material No encontrada.",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }else {
            this.materialOrderService.updateMaterialOrder(materialOrder,id,photoFile);
            MaterialOrder findOrder = this.materialOrderService.findById(id);
            response = new ApiResponse<>("Orden de material Actualizada",findOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

    }
    //Eliminar

    @DeleteMapping("/deleteMaterialOrder/{id}")
    public ResponseEntity<ApiResponse<MaterialOrder>> deleteMaterialOrder(@PathVariable Long id){
        ApiResponse<MaterialOrder> response;
        if (!this.materialOrderService.existMaterialOrderById(id)){
            response = new ApiResponse<>("Orden de material No encontrada.",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }else{
            this.materialOrderService.deleteMaterialOrder(id);
            response = new ApiResponse<>("Orden de material Eliminada.",null);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        }

    }


}
