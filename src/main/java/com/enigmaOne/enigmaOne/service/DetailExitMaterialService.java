package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.persistence.repository.DetailExitMaterialRepository;
import com.enigmaOne.enigmaOne.service.mapper.DetailExitMaterialMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DetailExitMaterialService implements DetailExitMaterialInterface {


    @Autowired
    private DetailExitMaterialRepository detailExitMaterialRepository;
    @Autowired
    private  DetailExitMaterialMapper detailExitMaterialMapper;

    @Override
    public List<DetailExitMaterial> getAllDetailExitMaterials() {
        return this.detailExitMaterialRepository.findAll();
    }

    @Override
    public DetailExitMaterial getDetailExitMaterialById(Long id) {
        return this.detailExitMaterialRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createDetailExitMaterial(DetailExitMaterial detailExitMaterial) {
        try {
            this.detailExitMaterialRepository.save(detailExitMaterial);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " +e);
            return  false;
        }
    }

    @Override
    @Transactional
    public boolean updateDetailExitMaterial(DetailExitMaterial detailExitMaterial,Long id) {
        try {
            DetailExitMaterial findDetailExitMaterial = this.getDetailExitMaterialById(id);

            this.detailExitMaterialMapper.updateDetailExitMaterialFromDto(detailExitMaterial,findDetailExitMaterial);
            this.detailExitMaterialRepository.save(findDetailExitMaterial);
            return true;

        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteDetailExitMaterial(Long id) {
        try {
            DetailExitMaterial detailExitMaterial = this.getDetailExitMaterialById(id);
            this.detailExitMaterialRepository.delete(detailExitMaterial);
            return true;
        }catch (Exception e){
            System.out.println("ERROR: " + e);
            return false;
        }
    }

    @Override
    public boolean existsDetailExitMaterialById(Long id) {
        return this.detailExitMaterialRepository.existsDetailExitMaterialById(id);
    }
}
