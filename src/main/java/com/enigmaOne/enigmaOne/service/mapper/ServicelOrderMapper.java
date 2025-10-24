package com.enigmaOne.enigmaOne.service.mapper;


import com.enigmaOne.enigmaOne.persistence.entity.MaterialOrder;
import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServicelOrderMapper {

    ServicelOrderMapper INSTANCE = Mappers.getMapper(ServicelOrderMapper.class);
    void updateServiceOrderFromDto(ServiceOrder serviceOrder, @MappingTarget ServiceOrder entity );

}
