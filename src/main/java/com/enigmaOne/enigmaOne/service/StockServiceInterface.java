package com.enigmaOne.enigmaOne.service;


import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StockServiceInterface {


    List<Stock> getAllStocks();
    Stock getStockById(Long id);
    boolean createStock(Stock stock, MultipartFile photo, MultipartFile orderGuides);
    boolean updateStock(Stock stock, Long id,MultipartFile photo, MultipartFile orderGuides);
    boolean deleteStock(Long id);

    // Sobrecargas para compatibilidad con código existente
    boolean createStock(Stock stock, MultipartFile photo);
    boolean updateStock(Stock stock, Long id, MultipartFile photo);

    // Nuevas sobrecargas: sin archivos para evitar llamadas con null ambiguo
    boolean createStock(Stock stock);
    boolean updateStock(Stock stock, Long id);

    boolean stockExistsById(Long id);
    boolean stockExistsByCode(String code);

    List<Stock> getStockForDescription(String description);
    List<Stock> getStockForCode(String code);
    List<Stock> getStockForId(Long id);
    List<Stock> findStockForWarehouseId(Long warehouseId);
}
