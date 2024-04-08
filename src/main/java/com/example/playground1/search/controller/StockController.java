package com.example.playground1.search.controller;


import com.example.playground1.search.model.Stock;
import com.example.playground1.search.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;



@RestController
@RequestMapping("/api/v1/stock/search/")
public class StockController {
    private final StockService stockService;
    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("{symbol}")
    public List<Stock> getStockData(@PathVariable String symbol) {
        // Check if stock data for the symbol exists in the database
        List<Stock> stocks = stockService.getStockDataBySymbol(symbol);

        if (stocks.isEmpty()) {
            // If no data exists, fetch data from Rapid API and save to database
            return stockService.fetchDataFromApiAndSaveToDatabase(symbol).join();
        }

        // If data exists in the database, return it
        return stocks;
    }

}
