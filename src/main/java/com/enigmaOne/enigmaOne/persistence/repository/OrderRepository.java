package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.Order;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderRepository extends ListCrudRepository<Order,Long> {

    boolean existsById(Long id);

    List<Order> getOrdersByEmployeeId(Long employeeId);
}
