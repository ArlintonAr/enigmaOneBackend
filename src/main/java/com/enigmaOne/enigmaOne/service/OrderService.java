package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.persistence.repository.OrderRepository;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;
import com.enigmaOne.enigmaOne.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enigmaOne.enigmaOne.Config.CustomUserDetails;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;
import com.enigmaOne.enigmaOne.service.dto.EmployeeResponseDTO;

import java.util.List;

@Service
public class OrderService implements OrderServiceInterface{


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TrackingService trackingService;

    @Autowired
    private OrderApprovalService orderApprovalService; // nueva inyección

    @Autowired
    private EmployeeService employeeService; // para resolver nombre del autor

    @Override
    public List<Order> getAllOrder() {
        List<Order> orders = this.orderRepository.findAll();
        return orders;
    }

    @Override
    public Order findById(Long id) {
        Order order = this.orderRepository.findById(id).orElse(null);
        return order;
    }

    @Override
    public boolean saveOrder(Order order) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //Agregar el id del usuario que crea la orden de forma segura
            Long employeeId = null;
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof CustomUserDetails) {
                    employeeId = ((CustomUserDetails) principal).getId();
                } else if (principal != null && !"anonymousUser".equals(principal.toString())) {
                    try {
                        employeeId = Long.parseLong(principal.toString());
                    } catch (NumberFormatException ex) {
                        employeeId = null; // no se pudo parsear
                    }
                }
            }
            order.setEmployeeId(employeeId);
            //Guardar la orden
             Order orderSaved = this.orderRepository.save(order);

            //Crear el tracking inicial con id de la orden creada (usar helper para consistencia)
            String creatorName = null;
            if (employeeId != null){
                EmployeeResponseDTO dto = this.employeeService.getEmployeeById(employeeId);
                if (dto != null) creatorName = dto.getFirstName() + " " + dto.getLastName();
            }
            // Create or update the single tracking record for this order (quick-fix: keep one record)
            this.trackingService.updateOrCreateTrackingEvent(orderSaved.getId(), TrackingState.PEDIDO, employeeId, creatorName, null);

            // Inicializar aprobaciones para la orden
            try {
                this.orderApprovalService.initializeApprovals(orderSaved);
            } catch (Exception e) {
                // no bloquear creación si falla init approvals, pero loguear
                System.out.println("WARN: no se pudieron inicializar aprobaciones: " + e);
            }

            return true;
        }catch (Exception e){
            System.out.println("ERROR: " +e);
            return  false;
        }
    }

    @Override
    @Transactional
    public boolean updateOrder(Order order,Long id) {
        try {
            Order findOrder = this.findById(id);
            order.setId(findOrder.getId());

            this.orderMapper.updateOrderFromDto(order,findOrder);
            this.orderRepository.save(findOrder);
            return true;

        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteOrder(Long id) {
        try {
            if(!this.existOrderById(id)){
                return false;
            }
            Order order = this.findById(id);


            //Eliminar los trackings asociados a la orden
            List<Tracking> trackings = order.getTrackings();
            for (Tracking tracking: trackings) {
               boolean response =  this.trackingService.deleteTracking(tracking.getId());
               if (!response){
                  throw new RuntimeException("No se pudo eliminar el tracking con id: " + tracking.getId());
               }
            }
            this.orderRepository.delete(order);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            throw  e;
        }
    }

    @Override
    public List<Order> getOrdersForEmployeeId(Long employeeId) {
        List<Order> orders = this.orderRepository.getOrdersByEmployeeId(employeeId)
                .stream()
                .toList();
        return  orders;
    }

    @Override
    public List<Order> getOrdersByApprovalStatus(ApprovalStatus approvalStatus) {
        List<Order> orders = this.orderRepository.getOrdersByApprovalStatus(approvalStatus)
                .stream()
                .toList();
        return orders;
    }

    public boolean existOrderById(Long id){
        if(!this.orderRepository.existsById(id)){
            return false;
        }
        return true;
    }

    /**
     * Agrega un evento de tracking para una orden, resolviendo el actor desde
     * el usuario autenticado si está disponible. Útil para transiciones RUTA/ALMACEN.
     */
    public boolean addTrackingEvent(Long orderId, TrackingState state, String note){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Long actorId = null;
            String actorName = null;
            if(authentication != null){
                Object principal = authentication.getPrincipal();
                if(principal instanceof CustomUserDetails){
                    actorId = ((CustomUserDetails) principal).getId();
                    // intentar resolver nombre
                    EmployeeResponseDTO dto = this.employeeService.getEmployeeById(actorId);
                    if(dto != null) actorName = dto.getFirstName() + " " + dto.getLastName();
                }
            }
            // update the single tracking record for this order (or create it if missing)
            return this.trackingService.updateOrCreateTrackingEvent(orderId, state, actorId, actorName, note);
        }catch (Exception e){
            System.out.println("ERROR addTrackingEvent: " + e);
            return false;
        }
    }
}
