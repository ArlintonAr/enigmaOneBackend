package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;

import com.enigmaOne.enigmaOne.persistence.repository.DetailEntryMaterialRepository;
import com.enigmaOne.enigmaOne.service.mapper.DetailEntryMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailEntryMaterialService implements DetailEntryMaterialInterface {

    @Autowired
    private DetailEntryMaterialRepository detailEntryMaterialRepository;

    @Autowired
    private DetailEntryMaterialMapper detailEntryMaterialMapper;

    @Override
    public List<DetailEntryMaterial> getAllDetailEntryMaterials() {
        return this.detailEntryMaterialRepository.findAll();
    }

    @Override
    public DetailEntryMaterial getDetailEntryMaterialById(Long id) {
        return this.detailEntryMaterialRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createDetailEntryMaterial(DetailEntryMaterial detailEntryMaterial) {
        try {
            this.detailEntryMaterialRepository.save(detailEntryMaterial);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " +e);
            return  false;
        }
    }

    @Override
    public boolean updateDetailEntryMaterial(DetailEntryMaterial detailEntryMaterial, Long id) {
        try {
            DetailEntryMaterial findDetailEntryMaterial = this.getDetailEntryMaterialById(id);

            this.detailEntryMaterialMapper.updateDetailEntryMaterialFromDto(detailEntryMaterial,findDetailEntryMaterial);
            this.detailEntryMaterialRepository.save(findDetailEntryMaterial);
            return true;

        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    public boolean deleteDetailEntryMaterial(Long id) {
        try {
            DetailEntryMaterial detailEntryMaterial = this.getDetailEntryMaterialById(id);
            this.detailEntryMaterialRepository.delete(detailEntryMaterial);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    public boolean existsDetailEntryMaterialById(Long id) {
        return this.detailEntryMaterialRepository.existsDetailEntryMaterialById(id);
    }
}
