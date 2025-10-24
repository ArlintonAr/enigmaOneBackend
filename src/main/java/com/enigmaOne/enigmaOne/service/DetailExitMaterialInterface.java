package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;

import java.util.List;

public interface DetailExitMaterialInterface {

    List<DetailExitMaterial> getAllDetailExitMaterials();
    DetailExitMaterial getDetailExitMaterialById(Long id);
    boolean createDetailExitMaterial(DetailExitMaterial  detailExitMaterial);
    boolean updateDetailExitMaterial(DetailExitMaterial detailExitMaterial,Long id);
    boolean deleteDetailExitMaterial(Long id );
    boolean existsDetailExitMaterialById(Long id);

}
