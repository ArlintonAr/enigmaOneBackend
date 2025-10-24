package com.enigmaOne.enigmaOne.persistence.repository;


import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import org.springframework.data.repository.ListCrudRepository;

public interface DetailExitMaterialRepository extends ListCrudRepository<DetailExitMaterial, Long> {
    boolean existsDetailExitMaterialById(Long id);

}