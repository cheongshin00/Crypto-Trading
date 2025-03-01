package com.crypto.trading.model;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "trading_pairs")
public class TradingPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "base_currency", nullable = false)
    private String baseCurrency;

    @Column(name = "quote_currency", nullable = false)
    private String quoteCurrency;

    @Column(name = "is_active")
    private boolean isActive = true;

    public String getPairSymbol() {
        return baseCurrency + quoteCurrency;
    }
}