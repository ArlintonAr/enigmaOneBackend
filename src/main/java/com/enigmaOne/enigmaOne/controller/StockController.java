package com.enigmaOne.enigmaOne.controller;

import com.enigmaOne.enigmaOne.persistence.entity.Stock;
import com.enigmaOne.enigmaOne.service.StockService;
import com.enigmaOne.enigmaOne.service.dto.ApiResponse;
import com.enigmaOne.enigmaOne.service.dto.ApiResponseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    private StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Stock>>> getAllStocks(){
        List<Stock> stocks = this.stockService.getAllStocks();

        if (stocks.isEmpty()){
            ApiResponse<List<Stock>> response = new ApiResponse<>("Stock vacío", stocks);
            return ResponseEntity.status(404).body(response);
        }else {
            ApiResponse<List<Stock>> response = new ApiResponse<>("Lista de Stocks", stocks);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseTest<List<Stock>>> getStockById(@PathVariable Long id){
        Stock stock = this.stockService.getStockById(id);
        List<Stock> stockList = List.of(stock);
        if (stock ==null){
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock no encontrado", stockList,404);
            return ResponseEntity.status(404).body(response);
        }else {
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock encontrado", stockList,200);
            return ResponseEntity.ok(response);
        }
    }
    @GetMapping("/searchForId/{id}")
    public ResponseEntity<ApiResponseTest<List<Stock>>> getStockForId(@PathVariable Long id){
        List<Stock> stocks = this.stockService.getStockForId(id);
        if (stocks.isEmpty()){
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock no encontrado", stocks,404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }else {
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock encontrado", stocks,200);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/searchForCode/{code}")
    public ResponseEntity<ApiResponseTest<List<Stock>>> getStockForCode(@PathVariable String code){
        List<Stock> stocks = this.stockService.getStockForCode(code);
        if (stocks.isEmpty()){
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock no encontrado", stocks,404);
            return ResponseEntity.status(404).body(response);
        }else {
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock encontrado", stocks,200);
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/searchForName/{description}")
    public ResponseEntity<ApiResponseTest<List<Stock>>> getStockForDescription(@PathVariable String description){
        List<Stock> stocks = this.stockService.getStockForDescription(description);
        if (stocks.isEmpty()){
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock no encontrado", stocks,404);
            return ResponseEntity.status(404).body(response);
        }else {
            ApiResponseTest<List<Stock>> response = new ApiResponseTest<>("Stock encontrado", stocks,200);
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping(value = "/createStock",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseTest<Stock>> createStock(
            @RequestPart("stock") Stock stock,
            @RequestPart(value = "photo", required = false) MultipartFile photo
    ){

        boolean existStockForCode = this.stockService.stockExistsByCode(stock.getCode());
        if (existStockForCode){
            ApiResponseTest<Stock> response = new ApiResponseTest<>("Stock ya existe con ese código", stock,409);
            return ResponseEntity.status(409).body(response);
        }

        boolean created = this.stockService.createStock(stock,photo);
        if (created){
            ApiResponseTest<Stock> response = new ApiResponseTest<>("Stock creado", stock,200);
            return ResponseEntity.status(201).body(response);
        }else {
            ApiResponseTest<Stock> response = new ApiResponseTest<>("Error al crear Stock", null,500);
            return ResponseEntity.status(500).body(response);
        }
    }

    @PatchMapping("/updateStock/{id}")
    public ResponseEntity<ApiResponse<Stock>> updateStock(
            @RequestPart("stock") Stock stock,
            @RequestPart(value = "photo", required = false) MultipartFile photo,
            @PathVariable Long id
    ){
        boolean existsById = this.stockService.stockExistsById(id);
        if (!existsById){
            ApiResponse<Stock> response = new ApiResponse<>("Stock no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }

        boolean updated = this.stockService.updateStock(stock, id, photo);
        if (updated){
            Stock updatedStock = this.stockService.getStockById(id);
            ApiResponse<Stock> response = new ApiResponse<>("Stock actualizado", updatedStock);
            return ResponseEntity.ok(response);
        }else {

            ApiResponse<Stock> response = new ApiResponse<>("Error al actualizar Stock", null);
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/deleteStock/{id}")
    public ResponseEntity<ApiResponse<Stock>> deleteStock(@PathVariable Long id){
        boolean existsById = this.stockService.stockExistsById(id);
        if (!existsById){
            ApiResponse<Stock> response = new ApiResponse<>("Stock no encontrado", null);
            return ResponseEntity.status(404).body(response);
        }
        boolean deleted = this.stockService.deleteStock(id);
        if (deleted){
            ApiResponse<Stock> response = new ApiResponse<>("Stock eliminado", null);
            return ResponseEntity.ok(response);
        }else {
            ApiResponse<Stock> response = new ApiResponse<>("Error al eliminar Stock", null);
            return ResponseEntity.status(500).body(response);
        }
    }


}
