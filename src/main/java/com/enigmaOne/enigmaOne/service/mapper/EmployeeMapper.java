package com.enigmaOne.enigmaOne.service.mapper;

import com.enigmaOne.enigmaOne.persistence.entity.Employee;
import com.enigmaOne.enigmaOne.service.dto.EmployeeCreateUpdateDTO;
import com.enigmaOne.enigmaOne.service.dto.EmployeeResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", //con esto componentModel se convierte en un componente de spring
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE //Con este valor ignorará los campos null que vienen del DTO y no escribe el valor existente en la entidad Employee con null
            )
public interface EmployeeMapper {

    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    @Mapping(source = "department.name",target = "departmentName")
    @Mapping(source = "position.positionName",target = "positionName")
    EmployeeResponseDTO toEmployeeDTOResponse(Employee employee);

    Employee DTOToEmployeeCreate(EmployeeCreateUpdateDTO employeeCreate);

    @Mapping(target = "id",ignore = true)//El id nunca se debe actualizar
    void DTOtoEmployeeUpdate(EmployeeCreateUpdateDTO employeeUpdate, @MappingTarget Employee entity);

}
