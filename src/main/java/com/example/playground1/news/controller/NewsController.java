package com.example.playground1.news.controller;

import com.example.playground1.news.model.StockNews;
import com.example.playground1.news.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/stock/news/")
public class NewsController {
    private final NewsService newsService;

    @Autowired
    public NewsController(NewsService newsService){
        this.newsService = newsService;
    }

    @GetMapping("{symbol}")
    public List<StockNews> getStockNews(@PathVariable String symbol){
        // Check if stock data for the symbol exists in the database
        List<StockNews> news= newsService.getNewsDataBySymbol(symbol);

        if (news.isEmpty()){
            // If no data exists, fetch data from Rapid API and save to database
            return newsService.fetchDataFromApiAndSaveToDatabase(symbol).join();
        }
        // If data exists in the database, return it
        return news;
    }



}
