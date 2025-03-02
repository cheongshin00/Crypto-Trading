package com.crypto.trading.service;

import com.crypto.trading.model.enums.Exchange;

import java.math.BigDecimal;
import java.util.Map;

public interface ExchangeService {
    /**
     * Fetches prices for supported trading pairs from the exchange
     * @return Map of symbol to [bidPrice, askPrice] values
     */
    Map<String, BigDecimal[]> fetchPrices();

    /**
     * Get the exchange name
     * @return Exchange enum value
     */
    Exchange getExchangeName();
}