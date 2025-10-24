package com.enigmaOne.enigmaOne.persistence.repository;


import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import org.springframework.data.repository.ListCrudRepository;

public interface ServiceOrderRepository extends ListCrudRepository<ServiceOrder, Long> {
    boolean existsById(Long id);
    boolean existsServiceOrderByCode(String code);
}
