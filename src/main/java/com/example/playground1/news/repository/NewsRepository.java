package com.example.playground1.news.repository;

import com.example.playground1.news.model.StockNews;
import com.example.playground1.search.model.Stock;
import  org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<StockNews, Long> {
    List<StockNews> findBySymbol(String symbol);

}
