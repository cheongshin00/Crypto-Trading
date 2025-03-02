package com.crypto.trading.model;

import lombok.Data;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "price_aggregation")
public class PriceAggregation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", nullable = false)
    private String symbol;

    @Column(name = "bid_price", precision = 24, scale = 2)
    private BigDecimal bidPrice;

    @Column(name = "ask_price", precision = 24, scale = 2)
    private BigDecimal askPrice;
    @Column(name = "bid_exchange")
    private String bidExchange;

    @Column(name = "ask_exchange" )
    private String askExchange;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    public PriceAggregation(){
    }

    public PriceAggregation(String symbol, BigDecimal bidPrice, BigDecimal askPrice,
                           String bidExchange, String askExchange, LocalDateTime timestamp) {
        this.symbol = symbol;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
        this.bidExchange = bidExchange;
        this.askExchange = askExchange;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(BigDecimal askPrice) {
        this.askPrice = askPrice;
    }

    public String getBidExchange() {
        return bidExchange;
    }

    public void setBidExchange(String bidExchange) {
        this.bidExchange = bidExchange;
    }

    public String getAskExchange() {
        return askExchange;
    }

    public void setAskExchange(String askExchange) {
        this.askExchange = askExchange;
    }


    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}