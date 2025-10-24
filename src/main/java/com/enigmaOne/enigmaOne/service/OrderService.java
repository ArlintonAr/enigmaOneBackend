package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.persistence.repository.OrderRepository;
import com.enigmaOne.enigmaOne.service.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.enigmaOne.enigmaOne.Config.CustomUserDetails;

import java.util.List;

@Service
public class OrderService implements OrderServiceInterface{


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TrackingService trackingService;

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

            //Crear el tracking inicial con id de la orden creada
            Tracking newTracking = new Tracking();
            newTracking.setOrderId(orderSaved.getId());
            this.trackingService.saveTracking(newTracking);
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

    public boolean existOrderById(Long id){
        if(!this.orderRepository.existsById(id)){
            return false;
        }
        return true;
    }
}
