package com.enigmaOne.enigmaOne.service;


import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StockServiceInterface {


    List<Stock> getAllStocks();
    Stock getStockById(Long id);
    boolean createStock(Stock stock, MultipartFile photo);
    boolean updateStock(Stock stock, Long id,MultipartFile photo);
    boolean deleteStock(Long id);

    boolean stockExistsById(Long id);
    boolean stockExistsByCode(String code);

    List<Stock> getStockForDescription(String description);
    List<Stock> getStockForCode(String code);
    List<Stock> getStockForId(Long id);
}
