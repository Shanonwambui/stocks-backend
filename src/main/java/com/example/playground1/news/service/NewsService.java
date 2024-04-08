package com.example.playground1.news.service;
import com.example.playground1.news.model.StockNews;
import com.example.playground1.news.repository.NewsRepository;

import com.example.playground1.search.model.Stock;
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
public class NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository newsRepository;
    private final AsyncHttpClient client;

    @Autowired
    public NewsService(NewsRepository newsRepository){
        this.newsRepository = newsRepository;
        this.client = new DefaultAsyncHttpClient();
    }

    @Cacheable("news")
    public CompletableFuture<List<StockNews>> fetchDataFromApiAndSaveToDatabase(String symbol) {
        String apiUrl = "https://yahoo-finance127.p.rapidapi.com/news/" + symbol;
        // Create an async HTTP client
        AsyncHttpClient client = new DefaultAsyncHttpClient();
        // Call the API and handle the response asynchronously
        return client.prepareGet(apiUrl)
                .setHeader("X-RapidAPI-Key", "e8abf209cbmshe3f60c54a62d769p160bd5jsnace99bbf415f")
                .setHeader("X-RapidAPI-Host", "yahoo-finance127.p.rapidapi.com")
                .execute()
                .toCompletableFuture()
                .thenApply(response -> {
                    String responseBody = response.getResponseBody();
                    logger.info("Response from API: {}", responseBody); // Log the response body

                    List<StockNews> news = parseResponseAndCreateNewsObjects(responseBody);
                    if (!news.isEmpty()){
                        for (StockNews stockNews: news){
                            newsRepository.saveAll(news);
                        }
                        logger.info("Data saved to the database successfully.");
                    }else {
                        logger.warn("No stock objects were created due to lack of keys in the API response.");

                    }
                    // close the Http client after processing the response
                    try {
                        client.close();
                    } catch (IOException e){
                        throw new RuntimeException("Failed to close the client", e);
                    }
                    return news;


        });

    }
    private List<StockNews> parseResponseAndCreateNewsObjects(String responseBody) {
        try {
            List<StockNews> news = new ArrayList<>();

            // Parse JSON response using ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode newsNode = rootNode.get(String.valueOf(i));

                String title = newsNode.path("title").asText();
                String publisher = newsNode.path("publisher").asText();
                String link = newsNode.path("link").asText();
                String type = newsNode.path("type").asText();

                // Create a new StockNews object and add it to the list
                StockNews stockNews = new StockNews();

                stockNews.setTitle(title);
                stockNews.setPublisher(publisher);
                stockNews.setLink(link);
                stockNews.setType(type);
                news.add(stockNews);
            }

        return news;
    }
        catch (IOException e) {
            logger.error("Error parsing JSON response: {}", e.getMessage());
            throw new RuntimeException("Error parsing JSON response", e);
        }
    }
    public List<StockNews> getNewsDataBySymbol(String symbol){
        return newsRepository.findBySymbol(symbol);
    }
    @Scheduled(fixedRate = 3600000) // Run every hour (3600000 milliseconds)
    public void updateDatabase() {
        List<StockNews> allStocks = newsRepository.findAll();
        for (StockNews stockNews : allStocks) {
            fetchDataFromApiAndSaveToDatabase(stockNews.getSymbol());
        }
        logger.info("Database updated successfully.");
    }
}
