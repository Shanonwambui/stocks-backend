package com.example.playground1.search.repository;
import com.example.playground1.search.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long>{
    List<Stock> findBySymbol(String symbol);

}
