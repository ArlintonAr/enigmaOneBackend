package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;

import java.util.List;

public interface WarehouseServiceInterface {

    List<Warehouse> getAllWarehouses();
    Warehouse getWarehouseById(Long id);
    boolean createWarehouse(Warehouse warehouse);
    boolean updateWarehouse(Long id, Warehouse warehouse);
    boolean deleteWarehouse(Long id);

    boolean existsWarehouseById(Long id);
}
