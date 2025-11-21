package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;
import com.enigmaOne.enigmaOne.persistence.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService implements WarehouseServiceInterface {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Override
    public List<Warehouse> getAllWarehouses() {
        return this.warehouseRepository.findAll();
    }

    @Override
    public Warehouse getWarehouseById(Long id) {
        return this.warehouseRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createWarehouse(Warehouse warehouse) {
        try {
            this.warehouseRepository.save(warehouse);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateWarehouse(Long id, Warehouse warehouse) {
        try {
            Warehouse found = this.getWarehouseById(id);
            if (found == null) return false;
            // actualizar campos simples
            found.setLocationName(warehouse.getLocationName());
            found.setLatitude(warehouse.getLatitude());
            found.setLongitude(warehouse.getLongitude());
            this.warehouseRepository.save(found);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteWarehouse(Long id) {
        try {
            Warehouse found = this.getWarehouseById(id);
            if (found == null) return false;
            this.warehouseRepository.delete(found);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsWarehouseById(Long id) {
        return this.warehouseRepository.existsWarehouseById(id);
    }
}
