package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;
import com.enigmaOne.enigmaOne.persistence.types.TrackingState;

import java.util.List;
import java.util.Optional;

public interface OrderServiceInterface {

     List<Order> getAllOrder();
     Order findById(Long id);
     boolean saveOrder(Order order);
     boolean updateOrder(Order order,Long id);
     boolean deleteOrder(Long id);

     List<Order> getOrdersForEmployeeId(Long employeeId);
     List<Order> getOrdersByApprovalStatus(ApprovalStatus approvalStatus);


    List<Order> findByCurrentTrackingState(TrackingState state);
    Optional<Order> findFirstByCurrentTrackingState(TrackingState state);
}
