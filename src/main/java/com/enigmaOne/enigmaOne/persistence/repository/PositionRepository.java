package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.Position;
import org.springframework.data.repository.ListCrudRepository;

public interface PositionRepository extends ListCrudRepository<Position,Long> {
    boolean existsPositionById(Long id);
    boolean existsPositionByCode(String code);
}
