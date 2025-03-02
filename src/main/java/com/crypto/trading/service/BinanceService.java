package com.crypto.trading.service;

import com.crypto.trading.model.enums.Exchange;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class BinanceService implements ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(BinanceService.class);
    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/bookTicker";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public BinanceService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches prices for supported trading pairs from Binance API
     * @return Map of symbol to [bidPrice, askPrice] values
     */
    public Map<String, BigDecimal[]> fetchPrices() {
        Map<String, BigDecimal[]> prices = new HashMap<>();

        try {
            // HTTP request to Binance API
            String response = restTemplate.getForObject(BINANCE_API_URL, String.class);

            // Parse the response
            JsonNode jsonArray = objectMapper.readTree(response);

            // Process each symbol in the response
            for (JsonNode item : jsonArray) {
                String symbol = item.get("symbol").asText();

                // Get ETHUSDT and BTCUSDT bid price and ask price
                if ("ETHUSDT".equals(symbol) || "BTCUSDT".equals(symbol)) {
                    BigDecimal bidPrice = new BigDecimal(item.get("bidPrice").asText()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal askPrice = new BigDecimal(item.get("askPrice").asText()).setScale(2, RoundingMode.HALF_UP);

                    // Store values in array [bidPrice, askPrice]
                    prices.put(symbol, new BigDecimal[]{bidPrice, askPrice});

                    logger.info("Binance price for {}: Bid={}, Ask={}",
                            symbol, bidPrice, askPrice);
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Error parsing Binance API response", e);
        } catch (Exception e) {
            logger.error("Error fetching prices from Binance", e);
        }

        return prices;
    }

    //return enum value for Binance
    public Exchange getExchangeName() {
        return Exchange.BINANCE;
    }
}