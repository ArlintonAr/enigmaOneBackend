package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.Config.GenerateNanoIdCongif;
import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import com.enigmaOne.enigmaOne.persistence.repository.ServiceOrderRepository;
import com.enigmaOne.enigmaOne.service.mapper.ServicelOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceOrderService implements ServiceOrderInterface{

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private GenerateNanoIdCongif generateNanoIdCongif;

    @Autowired
    private ServicelOrderMapper servicelOrderMapper;

    @Override
    public List<ServiceOrder> getAllServiceOrder() {
        return  this.serviceOrderRepository.findAll();
    }

    @Override
    public ServiceOrder findById(Long id) {
        return this.serviceOrderRepository.findById(id).orElse(null);
    }

    @Override
    public boolean saveServiceOrder(ServiceOrder serviceOrder) {
        try {

            serviceOrder.setCode(this.generateNanoIdCongif.generateNanoId());
            boolean existCode = this.existCodeInDataBase(serviceOrder.getCode()); //mejorar
            if (!existCode){

                this.serviceOrderRepository.save(serviceOrder);
                return true;
            }else {
                return  false;
            }
        }catch (Exception e){
            System.out.println("ERROR: " +e);
            return  false;
        }
    }

    @Override
    public boolean updateServiceOrder(ServiceOrder serviceOrder, Long id) {

        try {
            ServiceOrder findServiceOrder = this.findById(id);
            serviceOrder.setId(findServiceOrder.getId());

            this.servicelOrderMapper.updateServiceOrderFromDto(serviceOrder,findServiceOrder);
            this.serviceOrderRepository.save(findServiceOrder);

            System.out.println("Pasa por aqui el codigo ");
            return true;

        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteServiceOrder(Long id) {
        try {
            if(!this.existServiceOrderById(id)){
                return false;
            }
            ServiceOrder serviceOrder = this.findById(id);
            this.serviceOrderRepository.delete(serviceOrder);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }


    private boolean existCodeInDataBase(String code){
      return   this.serviceOrderRepository.existsServiceOrderByCode(code);
    }

    public boolean existServiceOrderById(Long id){
        return  this.serviceOrderRepository.existsById(id);
    }
}
