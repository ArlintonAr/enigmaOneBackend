package com.enigmaOne.enigmaOne.persistence.repository;


import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import org.springframework.data.repository.ListCrudRepository;

public interface DetailEntryMaterialRepository extends ListCrudRepository<DetailEntryMaterial, Long> {

    boolean existsDetailEntryMaterialById(Long id);

}