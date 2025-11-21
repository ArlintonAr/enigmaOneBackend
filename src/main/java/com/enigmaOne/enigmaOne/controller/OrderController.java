package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.entity.OrderApproval;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;
import com.enigmaOne.enigmaOne.service.OrderService;
import com.enigmaOne.enigmaOne.service.OrderApprovalService;
import com.enigmaOne.enigmaOne.service.JasperReportService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import com.enigmaOne.enigmaOne.service.dto.ApiResponseTest;
import com.enigmaOne.enigmaOne.service.dto.ApprovalActionDTO;
import com.enigmaOne.enigmaOne.service.dto.ApprovalResultDTO;
import com.enigmaOne.enigmaOne.service.dto.TrackingActionDTO;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalRole;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/orders")
public class OrderController {


    private final OrderService orderService;
    private final JasperReportService jasperReportService;

    @Autowired
    private OrderApprovalService orderApprovalService;

    @Autowired
    public OrderController(OrderService orderService, JasperReportService jasperReportService){
        this.orderService = orderService;
        this.jasperReportService = jasperReportService;
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

    @GetMapping("/trackingState/{state}")
    public ResponseEntity<ApiResponseTest<List<Order>>> getOrdersByTrackingState(@PathVariable TrackingState state){
        List<Order> orders = this.orderService.findByCurrentTrackingState(state);
        if(orders.isEmpty()){
            ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Ordenes vacías",null,404);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Lista de Ordenes por estado de seguimiento",orders,200);
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


    @GetMapping("/ordersByApprovalStatus/{status}")
    public ResponseEntity<ApiResponseTest<List<Order>>> getOrderByApprovalStatus(@PathVariable ApprovalStatus status){

        List<Order> orders= this.orderService.getOrdersByApprovalStatus(status);

        if (orders.isEmpty()){
            ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Ordenes no encontradas",null,404);
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponseTest<List<Order>> response = new ApiResponseTest<>("Ordenes encontradas",orders,200);
        return ResponseEntity.ok(response);

    }


    // Endpoint para generar reporte PDF de una orden (muestra inline)
    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> getOrderReport(@PathVariable("id") Long id) {
        byte[] pdf = jasperReportService.generateOrderReportPdf(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "order-" + id + ".pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
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

    // --- Aprobaciones ---

    @GetMapping("/{id}/approvals")
    public ResponseEntity<ApiResponseTest<List<OrderApproval>>> listApprovals(@PathVariable Long id){
        List<OrderApproval> approvals = this.orderApprovalService.listApprovals(id);
        if(approvals == null || approvals.isEmpty()){
            ApiResponseTest<List<OrderApproval>> response = new ApiResponseTest<>("Aprobaciones vacías",null,404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        ApiResponseTest<List<OrderApproval>> response = new ApiResponseTest<>("Lista de aprobaciones",approvals,200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/approvals/{role}/approve")
    public ResponseEntity<ApiResponseTest<ApprovalResultDTO>> approve(@PathVariable Long id, @PathVariable String role, @RequestBody ApprovalActionDTO action){
        try{
            ApprovalRole r = ApprovalRole.valueOf(role.toUpperCase());
            ApprovalResultDTO result = this.orderApprovalService.approve(id, r, action);
            ApiResponseTest<ApprovalResultDTO> response = new ApiResponseTest<>("Aprobado", result,200);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException ex){
            ApiResponseTest<ApprovalResultDTO> response = new ApiResponseTest<>("Role inválido", null,400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            ApiResponseTest<ApprovalResultDTO> response = new ApiResponseTest<>("Error al aprobar: " + e.getMessage(), null,409);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/{id}/approvals/{role}/reject")
    public ResponseEntity<ApiResponseTest<ApprovalResultDTO>> reject(@PathVariable Long id, @PathVariable String role, @RequestBody ApprovalActionDTO action){
        try{
            ApprovalRole r = ApprovalRole.valueOf(role.toUpperCase());
            ApprovalResultDTO result = this.orderApprovalService.reject(id, r, action);
            ApiResponseTest<ApprovalResultDTO> response = new ApiResponseTest<>("Rechazado", result,200);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException ex){
            ApiResponseTest<ApprovalResultDTO> response = new ApiResponseTest<>("Role inválido", null,400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            ApiResponseTest<ApprovalResultDTO> response = new ApiResponseTest<>("Error al rechazar: " + e.getMessage(), null,409);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    // --- Tracking state change (PEDIDO -> APROBADO -> RUTA -> ALMACEN)
    @PostMapping("/{id}/tracking")
    public ResponseEntity<ApiResponseTest<Boolean>> changeTrackingState(@PathVariable Long id, @RequestBody TrackingActionDTO action){
        try{
            TrackingState s = TrackingState.valueOf(action.getState().toUpperCase());
            boolean result = this.orderService.addTrackingEvent(id, s, action.getNote());
            ApiResponseTest<Boolean> response = new ApiResponseTest<>("Tracking actualizado", result,200);
            return ResponseEntity.ok(response);
        }catch (IllegalArgumentException ex){
            ApiResponseTest<Boolean> response = new ApiResponseTest<>("Estado inválido", null,400);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }catch (Exception e){
            ApiResponseTest<Boolean> response = new ApiResponseTest<>("Error al actualizar tracking: " + e.getMessage(), null,409);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }


}
