package com.enigmaOne.enigmaOne.service;

import com.enigmaOne.enigmaOne.Config.GenerateNanoIdCongif;
import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import com.enigmaOne.enigmaOne.persistence.repository.StockRepository;
import com.enigmaOne.enigmaOne.service.mapper.StockMapper;
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


    @Override
    public List<Stock> getAllStocks() {
        return this.stockRepository.findAll();
    }

    @Override
    public Stock getStockById(Long id) {
        return this.stockRepository.findById(id).orElse(null);
    }

    @Override
    public boolean createStock(Stock stock, MultipartFile photo) {
        try {
            //Subida de imagen a Cloudinary
            if (photo != null && !photo.isEmpty() ){
                String photoUrl = cloudinaryService.uploadPhotoToCloudinary(photo, "stocks");
                stock.setPhoto(photoUrl);
            }

            if(stock.getCode()==null || stock.getCode().isEmpty()){
                String generatedCode = generateNanoIdCongif.generateNanoId();
                while (this.stockExistsByCode(generatedCode)){
                    generatedCode = generateNanoIdCongif.generateNanoId();
                }
                stock.setCode(generatedCode);
            }

            this.stockRepository.save(stock);
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateStock(Stock stock, Long id,MultipartFile photo ) {

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

            this.stockMapper.updateStockFromDto(stock, findStock);
            this.stockRepository.save(findStock);
            return true;

       } catch (Exception e) {
           System.out.println(e.getMessage());
           return false;
       }
    }

    @Override
    public boolean deleteStock(Long id) {
        try {
            Stock findStock = this.getStockById(id);
            if (findStock == null){
                return false;
            }
            this.cloudinaryService.deletePhotoOnCloudinary(findStock.getPhoto());
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


}
