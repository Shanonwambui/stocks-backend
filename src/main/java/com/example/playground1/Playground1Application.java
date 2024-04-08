package com.example.playground1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.concurrent.ExecutionException;
import com.example.playground1.search.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;




@SpringBootApplication
public class Playground1Application  {

    public static void main(String[] args) throws InterruptedException, ExecutionException  {
        SpringApplication.run(Playground1Application.class, args);

    }
    @Component
    public static class DataInitializer implements CommandLineRunner {
        private final StockService stockService;

        @Autowired
        public DataInitializer(StockService stockService) {
            this.stockService = stockService;
        }

        @Override
        public void run(String... args) throws Exception {

        }
    }


}
