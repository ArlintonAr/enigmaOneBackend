package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.Config.GenerateNanoIdCongif;
import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import com.enigmaOne.enigmaOne.persistence.entity.Order;
import com.enigmaOne.enigmaOne.persistence.repository.MaterialOrderRepository;
import com.enigmaOne.enigmaOne.service.mapper.MaterialOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@Service
public class MaterialOrderService implements MaterialOrderInterface{


     @Autowired
     private  MaterialOrderRepository materialOrderRepository;

     @Autowired
     private MaterialOrderMapper materialOrderMapper;

     @Autowired
    private GenerateNanoIdCongif generateNanoIdCongif;

     @Autowired
     private CloudinaryService cloudinaryService;

    @Override
    public List<MaterialOrder> getAllMaterialOrder() {
        return this.materialOrderRepository.findAll();
    }

    @Override
    public MaterialOrder findById(Long id) {
       return  this.materialOrderRepository.findById(id).orElse(null);
    }

    @Override
    public boolean saveMaterialOrder(MaterialOrder materialOrder, MultipartFile photoFile) {
        try {

            materialOrder.setCode(this.generateNanoIdCongif.generateNanoId());
            boolean existCode = this.existCodeInDataBase(materialOrder.getCode()); //mejorar
            if (!existCode){
                if (photoFile != null && !photoFile.isEmpty()){
                    String urlPhoto = this.cloudinaryService.uploadPhotoToCloudinary(photoFile,"materials");
                    materialOrder.setPhoto(urlPhoto);
                }
                this.materialOrderRepository.save(materialOrder);
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
    public boolean updateMaterialOrder(MaterialOrder materialOrder, Long id,MultipartFile photoFile) {
        try {
            MaterialOrder findMaterialOrder = this.findById(id);
            materialOrder.setId(findMaterialOrder.getId());

            if (photoFile != null && !photoFile.isEmpty()){
                String urlPhoto = this.cloudinaryService.uploadPhotoToCloudinary(photoFile,"materials");
                materialOrder.setPhoto(urlPhoto);
            }

            this.materialOrderMapper.updateMaterialOrderFromDto(materialOrder,findMaterialOrder);
            this.materialOrderRepository.save(findMaterialOrder);

            return true;

        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteMaterialOrder(Long id) {
        try {
            if(!this.existMaterialOrderById(id)){
                return false;
            }
            MaterialOrder materialOrder = this.findById(id);
            this.cloudinaryService.deletePhotoOnCloudinary(materialOrder.getPhoto());
            this.materialOrderRepository.delete(materialOrder);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }
    public boolean existMaterialOrderById(Long id){
        return this.materialOrderRepository.existsById(id);
    }


    public boolean existCodeInDataBase(String code){
        return  this.materialOrderRepository.existsMaterialOrderByCode(code);

    }

}
