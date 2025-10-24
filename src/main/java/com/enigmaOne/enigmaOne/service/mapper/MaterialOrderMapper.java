package com.enigmaOne.enigmaOne.service.mapper;



import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;

import org.mapstruct.Mapper;

import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaterialOrderMapper {




    MaterialOrderMapper INSTANCE = Mappers.getMapper(MaterialOrderMapper.class);
    void updateMaterialOrderFromDto(MaterialOrder materialOrder, @MappingTarget MaterialOrder entity );

}
