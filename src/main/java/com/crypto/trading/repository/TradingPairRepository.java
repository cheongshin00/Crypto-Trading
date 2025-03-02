package com.crypto.trading.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crypto.trading.model.TradingPair;

@Repository
public interface TradingPairRepository extends JpaRepository<TradingPair, Long> {
    TradingPair findByBaseCurrencyAndQuoteCurrency(String baseCurrency, String quoteCurrency);
}