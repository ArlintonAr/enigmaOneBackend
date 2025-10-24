package com.enigmaOne.enigmaOne.service.mapper;


import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StockMapper {

    StockMapper INSTANCE = Mappers.getMapper(StockMapper.class);
    void updateStockFromDto(Stock stock, @MappingTarget Stock entity );

}
