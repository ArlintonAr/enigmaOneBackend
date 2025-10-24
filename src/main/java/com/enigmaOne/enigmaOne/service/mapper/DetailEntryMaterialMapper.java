package com.enigmaOne.enigmaOne.service.mapper;


import com.enigmaOne.enigmaOne.persistence.entity.DetailEntryMaterial;
import com.enigmaOne.enigmaOne.persistence.entity.DetailExitMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DetailEntryMaterialMapper {

    DetailEntryMaterialMapper INSTANCE = Mappers.getMapper(DetailEntryMaterialMapper.class);
    void updateDetailEntryMaterialFromDto(DetailEntryMaterial detailEntryMaterial, @MappingTarget DetailEntryMaterial entity );

}
