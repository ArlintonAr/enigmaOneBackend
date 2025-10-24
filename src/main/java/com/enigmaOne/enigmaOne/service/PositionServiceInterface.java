package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.persistence.entity.Position;

import java.util.List;

public interface PositionServiceInterface {

    List<Position> getAllPositions();
    Position getPositionById(Long id);
    boolean createPosition(Position position);
    boolean updatePosition(Long id, Position position);
    boolean deletePosition(Long id);

    boolean existsPositionById(Long code);
    boolean existsPositionByCode(String code);

}
