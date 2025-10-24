package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.service.OrderService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import com.enigmaOne.enigmaOne.service.dto.ApiResponseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/orders")
public class OrderController {


    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping()
    public ResponseEntity<ApiResponseTest<List<Order>>> getAllOrders(){
        List<Order> orders = this.orderService.getAllOrder();
        if(orders.isEmpty()){
            ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Ordenes vacías",null,404);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Lista de Ordenes",orders,200);
        return ResponseEntity.ok( response);
    }

    @GetMapping("/ordersByEmployeeId/{id}")
    public  ResponseEntity<ApiResponseTest<List<Order>>> getOrdersForEmployeeId(@PathVariable Long id ){
        List<Order> orders = this.orderService.getOrdersForEmployeeId(id);
        if(orders.isEmpty()){
            ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Ordenes vacías",null,404);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Lista de Ordenes por empleado",orders,200);
        return ResponseEntity.ok( response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id){
          Order order= this.orderService.findById(id);
          if (order==null){
                ApiResponse<Order> response = new ApiResponse<>("Orden no encontrada",null);
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
          }
          ApiResponse<Order> response = new ApiResponse<>("Orden encontrada",order);
        return ResponseEntity.ok(response);
    }
    //guardar
    @PostMapping("/createOrder")
    public ResponseEntity<ApiResponseTest<Order>> createOrder(@RequestBody Order order){

        if (!this.orderService.saveOrder(order)){
            ApiResponseTest<Order> response = new ApiResponseTest<>("Orden NO creada",order,204);
           return ResponseEntity.ok(response);
        }
         ApiResponseTest<Order> response = new ApiResponseTest<>("Orden Creada",order,200);
        return ResponseEntity.ok(response);
    }
    //Actualizar
    @PatchMapping("/updateOrder/{id}")
    public ResponseEntity<ApiResponse<Order>> updateOrder(@PathVariable Long id,@RequestBody Order order){
        ApiResponse<Order> response;
        if (!this.orderService.existOrderById(id)){
            response = new ApiResponse<>("Orden No encontrada.",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }else {
            this.orderService.updateOrder(order,id);
            Order findOrder = this.orderService.findById(id);
            response = new ApiResponse<>("Orden Actualizada",findOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

    }
    //Eliminar

    @DeleteMapping("/deleteOrder/{id}")
    public ResponseEntity<ApiResponse<Order>> deleteOrder(@PathVariable Long id){
        ApiResponse<Order> response;
        if (!this.orderService.existOrderById(id)){
            response = new ApiResponse<>("Orden No encontrada.",null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }else{
            this.orderService.deleteOrder(id);
            response = new ApiResponse<>("Orden Eliminada.",null);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        }

    }


}
