package com.enigmaOne.enigmaOne.service.mapper;


import com.enigmaOne.enigmaOne.persistence.entity.ServiceOrder;
import com.enigmaOne.enigmaOne.persistence.entity.Tracking;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TrackingMapper {

    TrackingMapper INSTANCE = Mappers.getMapper(TrackingMapper.class);
    void updateTrackingFromDto(Tracking tracking, @MappingTarget Tracking entity );

}
