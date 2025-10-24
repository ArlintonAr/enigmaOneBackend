package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import com.enigmaOne.enigmaOne.persistence.entity.Order;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MaterialOrderInterface {

    List<MaterialOrder> getAllMaterialOrder();
    MaterialOrder findById(Long id);
    boolean saveMaterialOrder(MaterialOrder materialOrder, MultipartFile photoFile);
    boolean updateMaterialOrder(MaterialOrder materialOrder,Long id,MultipartFile photoFile);
    boolean deleteMaterialOrder(Long id);
}
