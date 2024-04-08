package com.example.playground1.search.model;

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
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private long Id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "shortname")
    private String shortname;

    @Column(name = "exchange")
    private String exchange;

}
