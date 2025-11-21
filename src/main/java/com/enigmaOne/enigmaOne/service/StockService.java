package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.Config.GenerateNanoIdCongif;
import com.enigmaOne.enigmaOne.Config.FileStorageManager;
import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import com.enigmaOne.enigmaOne.persistence.entity.Warehouse;
import com.enigmaOne.enigmaOne.persistence.repository.StockRepository;
import com.enigmaOne.enigmaOne.persistence.repository.WarehouseRepository;
import com.enigmaOne.enigmaOne.service.mapper.StockMapper;
import com.enigmaOne.enigmaOne.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class StockService implements StockServiceInterface {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private GenerateNanoIdCongif generateNanoIdCongif;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private FileStorageManager fileStorageManager;


    @Override
    public List<Stock> getAllStocks() {
        return this.stockRepository.findAll();
    }

    @Override
    public Stock getStockById(Long id) {
        return this.stockRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createStock(Stock stock, MultipartFile photo, MultipartFile orderGuides) {
        try {
            //Subida de imagen a Cloudinary
            if (photo != null && !photo.isEmpty() ){
                String photoUrl = cloudinaryService.uploadPhotoToCloudinary(photo, "stocks");
                stock.setPhoto(photoUrl);
            }

            // Subida de PDF u otro archivo (orderGuides) usando FileStorageManager
            if (orderGuides != null && !orderGuides.isEmpty()){
                FileStorageService storage = fileStorageManager.getActiveService();
                String url = storage.uploadFile(orderGuides, "stocks/orderGuides");
                stock.setOrderGuides(url);
            }

            if(stock.getCode()==null || stock.getCode().isEmpty()){
                String generatedCode = generateNanoIdCongif.generateNanoId();
                while (this.stockExistsByCode(generatedCode)){
                    generatedCode = generateNanoIdCongif.generateNanoId();
                }
                stock.setCode(generatedCode);
            }

            // Si el request trae warehouseId o un objeto warehouse con id, vincular la entidad
            Warehouse resolvedWarehouse = null;
            if (stock.getWarehouse() != null && stock.getWarehouse().getId() != null) {
                resolvedWarehouse = this.warehouseRepository.findById(stock.getWarehouse().getId()).orElse(null);
            } else if (stock.getWarehouseId() != null) {
                resolvedWarehouse = this.warehouseRepository.findById(stock.getWarehouseId()).orElse(null);
            }
            if (resolvedWarehouse != null) {
                stock.setWarehouse(resolvedWarehouse);
            }

            this.stockRepository.save(stock);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    // Sobrecarga compatible: firma antigua que no incluye orderGuides
    @Override
    public boolean createStock(Stock stock, MultipartFile photo) {
        return createStock(stock, photo, null);
    }

    // Nueva sobrecarga: crear sin archivos
    @Override
    public boolean createStock(Stock stock) {
        return createStock(stock, null, null);
    }

    @Override
    @Transactional
    public boolean updateStock(Stock stock, Long id,MultipartFile photo, MultipartFile orderGuides ) {

       try {
           Stock findStock = this.getStockById(id);
           if (findStock == null){
               return false;
           }

           //Subida de imagen a Cloudinary
           if (photo != null && !photo.isEmpty()  ){
               Stock findStockWithPhoto = this.getStockById(id);

               this.cloudinaryService.deletePhotoOnCloudinary(findStockWithPhoto.getPhoto());

               String photoUrl = cloudinaryService.uploadPhotoToCloudinary(photo, "stocks");
               stock.setPhoto(photoUrl);
           }

           // Subida/actualización de orderGuides usando FileStorageManager
           if (orderGuides != null && !orderGuides.isEmpty()){
               FileStorageService storage = fileStorageManager.getActiveService();
               // eliminar archivo antiguo si existe
               String existing = findStock.getOrderGuides();
               if (existing != null && !existing.isEmpty()){
                   try { storage.deleteFile(existing); } catch (Exception ignored) {}
               }
               String url = storage.uploadFile(orderGuides, "stocks/orderGuides");
               stock.setOrderGuides(url);
           }

            // mapear campos recibidos hacia la entidad encontrada
            this.stockMapper.updateStockFromDto(stock, findStock);

            // Si en el DTO viene warehouseId o warehouse.id, resolver y asignar
            Warehouse resolvedWarehouse = null;
            if (stock.getWarehouse() != null && stock.getWarehouse().getId() != null) {
                resolvedWarehouse = this.warehouseRepository.findById(stock.getWarehouse().getId()).orElse(null);
            } else if (stock.getWarehouseId() != null) {
                resolvedWarehouse = this.warehouseRepository.findById(stock.getWarehouseId()).orElse(null);
            }
            if (resolvedWarehouse != null) {
                findStock.setWarehouse(resolvedWarehouse);
            }

            this.stockRepository.save(findStock);
            return true;

       } catch (Exception e) {
           System.out.println(e.getMessage());
           return false;
       }
    }

    // Sobrecarga compatible: firma antigua updateStock(stock,id,photo)
    @Override
    public boolean updateStock(Stock stock, Long id, MultipartFile photo) {
        return updateStock(stock, id, photo, null);
    }

    // Nueva sobrecarga: actualizar sin archivos
    @Override
    public boolean updateStock(Stock stock, Long id) {
        return updateStock(stock, id, null, null);
    }

    @Override
    public boolean deleteStock(Long id) {
        try {
            Stock findStock = this.getStockById(id);
            if (findStock == null){
                return false;
            }
            this.cloudinaryService.deletePhotoOnCloudinary(findStock.getPhoto());
            // eliminar orderGuides desde el storage activo
            try{
                FileStorageService storage = fileStorageManager.getActiveService();
                storage.deleteFile(findStock.getOrderGuides());
            }catch (Exception ignored){ }

            this.stockRepository.delete(findStock);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean stockExistsById(Long id) {
        return this.stockRepository.existsStockById(id);
    }

    @Override
    public boolean stockExistsByCode(String code) {
        return this.stockRepository.existsStockByCode(code);
    }

    @Override
    public List<Stock> getStockForDescription(String description) {
        return this.stockRepository.findStockByDescriptionContainingIgnoreCase(description)
                .stream().toList();
    }

    @Override
    public List<Stock> getStockForCode(String code) {
        return this.stockRepository.findStockByCode(code).stream().toList();
    }

    @Override
    public List<Stock> getStockForId(Long id) {
        return this.stockRepository.findStockById(id).stream().toList();
    }

    @Override
    public List<Stock> findStockForWarehouseId(Long warehouseId) {
        return this.stockRepository.findStockByWarehouseId(warehouseId).stream().toList();
    }


}
