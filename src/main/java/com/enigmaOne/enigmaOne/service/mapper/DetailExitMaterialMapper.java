package com.enigmaOne.enigmaOne.service.mapper;


import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DetailExitMaterialMapper {

    DetailExitMaterialMapper INSTANCE = Mappers.getMapper(DetailExitMaterialMapper.class);
    void updateDetailExitMaterialFromDto(DetailExitMaterial detailExitMaterial, @MappingTarget DetailExitMaterial entity );

}
