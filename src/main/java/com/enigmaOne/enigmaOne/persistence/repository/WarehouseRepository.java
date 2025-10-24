package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;
import org.springframework.data.repository.ListCrudRepository;

public interface WarehouseRepository extends ListCrudRepository<Warehouse,Long> {
    boolean existsWarehouseById(Long id);
}
