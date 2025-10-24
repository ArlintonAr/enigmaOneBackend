package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.Config.GenerateNanoIdCongif;
import com.enigmaOne.enigmaOne.persistence.entity.Position;
import com.enigmaOne.enigmaOne.persistence.repository.PositionRepository;
import com.enigmaOne.enigmaOne.service.mapper.PositionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService implements PositionServiceInterface{

    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private GenerateNanoIdCongif generateNanoIdCongif;
    @Autowired
    private PositionMapper positionMapper;


    @Override
    public List<Position> getAllPositions() {
        return this.positionRepository.findAll();
    }

    @Override
    public Position getPositionById(Long id) {
        return this.positionRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createPosition(Position position) {
        try {
            String code = generateNanoIdCongif.generateNanoId();
            position.setCode(code);
            this.positionRepository.save(position);
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    @Override
    public boolean updatePosition(Long id, Position position) {
        try {
            if (position.getCode()!=null){ //TODO:Validar para todas las entidades que no se pueda actualizar el codigo
                return false;
            }
            Position existingPosition = this.getPositionById(id);

            this.positionMapper.updatePositionFromDto(position, existingPosition);
            this.positionRepository.save(existingPosition);
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletePosition(Long id) {
        try {
            if (!this.existsPositionById(id)){
                return false;
            }
            this.positionRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    @Override
    public boolean existsPositionById(Long id) {
        return this.positionRepository.existsPositionById(id);
    }

    @Override
    public boolean existsPositionByCode(String code) {
        return this.positionRepository.existsPositionByCode(code);
    }
}
