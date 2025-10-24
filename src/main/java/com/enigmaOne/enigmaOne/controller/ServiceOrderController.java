package com.enigmaOne.enigmaOne.controller;


import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import com.enigmaOne.enigmaOne.service.ServiceOrderService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/serviceOrders")
public class ServiceOrderController {

    private ServiceOrderService serviceOrderService;

    @Autowired
    public ServiceOrderController(ServiceOrderService serviceOrderService){
        this.serviceOrderService = serviceOrderService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ServiceOrder>>> getAllServiceOrder(){
        List<ServiceOrder> serviceOrders = this.serviceOrderService.getAllServiceOrder();

        if(serviceOrders.isEmpty()){
            ApiResponse<List<ServiceOrder>> response = new ApiResponse<>("Ordenes de Servicio vacío",null);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<List<ServiceOrder>> response = new ApiResponse<>("Lista de Ordenes de servicio",serviceOrders);
        return ResponseEntity.ok( response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceOrder>> getServiceOrderById(@PathVariable Long id){
        ServiceOrder serviceOrder= this.serviceOrderService.findById(id);
        if (serviceOrder==null){

            ApiResponse<ServiceOrder> response = new ApiResponse<>("Orden de material NO encontrada",null);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponse<ServiceOrder> response = new ApiResponse<>("Orden encontrada",serviceOrder);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/createServiceOrder")
    public ResponseEntity<ApiResponse<ServiceOrder>> createServiceOrder(
            @RequestBody ServiceOrder serviceOrder
           )
    {
        boolean savedServiceOrder = this.serviceOrderService.saveServiceOrder(serviceOrder);
        ApiResponse<ServiceOrder> response;
        if (!savedServiceOrder){
            response = new ApiResponse<>("Orden de Servicio NO creada", serviceOrder);
        }else{
            response = new ApiResponse<>("Orden de Servicio Creada", serviceOrder);
        }
        return ResponseEntity.ok(response);
    }

    //Actualizar
    @PatchMapping("/updateServiceOrder/{id}")
    public ResponseEntity<ApiResponse<ServiceOrder>> updateServiceOrder(
            @PathVariable Long id,
            @RequestBody ServiceOrder serviceOrder

    ){
        ApiResponse<ServiceOrder> response;
        if (!this.serviceOrderService.existServiceOrderById(id)){
            response = new ApiResponse<>("Orden de material No encontrada.",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }else {
            this.serviceOrderService.updateServiceOrder(serviceOrder,id);

            ServiceOrder findOrder = this.serviceOrderService.findById(id);
            response = new ApiResponse<>("Orden de material Actualizada",findOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

    }
    //Eliminar
    @DeleteMapping("/deleteServiceOrder/{id}")
    public ResponseEntity<ApiResponse<ServiceOrder>> deleteServiceOrder(@PathVariable Long id){
        ApiResponse<ServiceOrder> response;
        if (!this.serviceOrderService.existServiceOrderById(id)){

            response = new ApiResponse<>("Orden de servicio No encontrada.",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }else{
            this.serviceOrderService.deleteServiceOrder(id);
            response = new ApiResponse<>("Orden de servicio Eliminada.",null);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        }

    }

}
