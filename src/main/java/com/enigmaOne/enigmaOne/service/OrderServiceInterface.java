package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.types.ApprovalStatus;

import java.util.List;

public interface OrderServiceInterface {

     List<Order> getAllOrder();
     Order findById(Long id);
     boolean saveOrder(Order order);
     boolean updateOrder(Order order,Long id);
     boolean deleteOrder(Long id);

     List<Order> getOrdersForEmployeeId(Long employeeId);
     List<Order> getOrdersByApprovalStatus(ApprovalStatus approvalStatus);
}
