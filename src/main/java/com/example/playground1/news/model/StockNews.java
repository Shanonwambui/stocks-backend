package com.example.playground1.news.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long Id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name  = "title")
    private String title;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "link")
    private String link;

    @Column(name = "type")
    private String type;
}
