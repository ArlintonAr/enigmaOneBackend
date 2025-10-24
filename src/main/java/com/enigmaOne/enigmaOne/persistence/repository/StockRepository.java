package com.enigmaOne.enigmaOne.persistence.repository;

import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface StockRepository extends ListCrudRepository<Stock,Long> {

    boolean existsStockById(Long id);
    boolean existsStockByCode(String code);

    List<Stock> findStockByCode(String code);
    List<Stock> findStockByDescriptionContainingIgnoreCase(String description);

    List<Stock> findStockById(Long id);

}
