package com.enigmaOne.enigmaOne.service.mapper;


import com.enigmaOne.enigmaOne.persistence.entity.Movement;
import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import com.enigmaOne.enigmaOne.service.dto.MovementResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MovementMapper {

    //@Mapping(source = "department.name",target = "departmentName")
    @Mapping(source = "employee.firstName" ,target = "employeeFirstName")
    @Mapping(source = "employee.lastName" ,target = "employeeLastName")
    @Mapping(source = "movement.materialRequerterFirstName",target = "materialRequesterFirstName")
    @Mapping(source = "movement.materialRequerterLastName",target = "materialRequesterLastName")
    MovementResponseDTO toMovementDTOResponse(Movement movement);

    MovementMapper INSTANCE = Mappers.getMapper(MovementMapper.class);
    void updateMovementFromDto(Movement movement, @MappingTarget Movement entity );

}
