package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import org.springframework.data.repository.ListCrudRepository;

public interface MaterialOrderRepository extends ListCrudRepository<MaterialOrder, Long> {
    boolean existsById(Long id);
    boolean existsMaterialOrderByCode(String code);
}
