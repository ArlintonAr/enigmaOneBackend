package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.Config.GenerateNanoIdCongif;
import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;
import com.enigmaOne.enigmaOne.persistence.repository.WarehouseRepository;
import com.enigmaOne.enigmaOne.service.mapper.WarehouseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService implements WarehouseServiceInterface{

    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private WarehouseMapper warehouseMapper;

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
      }catch (Exception e) {
          System.out.println("ERROR: " + e);
          return false;
      }
    }

    @Override
    public boolean updateWarehouse(Long id, Warehouse warehouse) {
        try {
            Warehouse findWarehouse = this.getWarehouseById(id);

            this.warehouseMapper.updateWarehouseFromDto(warehouse,findWarehouse);
            this.warehouseRepository.save(findWarehouse);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteWarehouse(Long id) {
        try {
            Warehouse findWarehouse = this.getWarehouseById(id);
            this.warehouseRepository.delete(findWarehouse);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    public boolean existsWarehouseById(Long id) {
        return this.warehouseRepository.existsWarehouseById(id);
    }
}
