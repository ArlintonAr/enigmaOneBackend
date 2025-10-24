package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;

import java.util.List;

public interface DetailEntryMaterialInterface {

    List<DetailEntryMaterial> getAllDetailEntryMaterials();
    DetailEntryMaterial getDetailEntryMaterialById(Long id);

    boolean createDetailEntryMaterial(DetailEntryMaterial detailEntryMaterial);
    boolean updateDetailEntryMaterial(DetailEntryMaterial detailEntryMaterial,Long id);
    boolean deleteDetailEntryMaterial(Long id );

    boolean existsDetailEntryMaterialById(Long id);

}
