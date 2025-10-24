package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ServiceOrderInterface {

    List<ServiceOrder> getAllServiceOrder();
    ServiceOrder findById(Long id);
    boolean saveServiceOrder(ServiceOrder serviceOrder);
    boolean updateServiceOrder(ServiceOrder serviceOrder,Long id);
    boolean deleteServiceOrder(Long id);
}
