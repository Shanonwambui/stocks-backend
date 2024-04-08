package com.example.playground1.search.service;

import com.example.playground1.search.model.Stock;
import com.example.playground1.search.repository.StockRepository;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.*;

@Service
public class StockService {
    private static final Logger logger = LoggerFactory.getLogger(StockService.class);
    private final StockRepository stockRepository;
    private final AsyncHttpClient client;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.client = new DefaultAsyncHttpClient();
    }
    @Cacheable("stocks")
    public CompletableFuture<List<Stock>> fetchDataFromApiAndSaveToDatabase(String symbol) {
        String apiUrl = "https://yahoo-finance127.p.rapidapi.com/search/" + symbol;

        // Create an async HTTP client
        AsyncHttpClient client = new DefaultAsyncHttpClient();

        // Call the API and handle the response asynchronously
        return client.prepareGet(apiUrl)
                .setHeader("X-RapidAPI-Key", "e8abf209cbmshe3f60c54a62d769p160bd5jsnace99bbf415f")
                .setHeader("X-RapidAPI-Host", "yahoo-finance127.p.rapidapi.com")
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    // Here you can process the response and save data to the database
                    String responseBody = response.getResponseBody();
                    logger.info("Response from API: {}", responseBody); // Log the response body

                    List<Stock> stocks = parseResponseAndCreateStockObjects(responseBody);
                    if (!stocks.isEmpty()) {
                        for (Stock stock : stocks) {
                            stockRepository.saveAll(stocks);
                        }
                        logger.info("Data saved to the database successfully.");
                    } else {
                        logger.warn("No stock objects were created due to lack of quotes in the API response.");
                    }

                    // Close the HTTP client after processing the response
                    try {
                        client.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to close the client", e);
                    }
                    return stocks;
                });
    }
    private List<Stock> parseResponseAndCreateStockObjects(String responseBody) {
        try {
            List<Stock> stocks = new ArrayList<>();

            // Parse JSON response using ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Extract relevant fields from JSON response
            JsonNode quotesNode = rootNode.get("quotes");
            if (quotesNode != null && quotesNode.isArray() && quotesNode.size() > 0) {
                for (JsonNode quoteNode : quotesNode) {
                    String symbol = quoteNode.get("symbol").asText();
                    String shortname = quoteNode.get("shortname").asText();
                    String exchange = quoteNode.get("exchange").asText();

                    // Create and add a new StockNews object to the list
                    Stock stock = new Stock();
                    stock.setSymbol(symbol);
                    stock.setShortname(shortname);
                    stock.setExchange(exchange);
                    stocks.add(stock);
                }
            } else {
                logger.warn("No quotes found in the API response.");
            }

            return stocks;
        } catch (IOException e) {
            logger.error("Error parsing JSON response: {}", e.getMessage());
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }

    public List<Stock> getStockDataBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol);
    }

    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 milliseconds)
    public void updateDatabase() {
        List<Stock> allStocks = stockRepository.findAll();
        for (Stock stock : allStocks) {
            fetchDataFromApiAndSaveToDatabase(stock.getSymbol());
        }
        logger.info("Database updated successfully.");
    }

}
