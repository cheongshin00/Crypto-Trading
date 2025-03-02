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
public class HuobiService implements ExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(HuobiService.class);
    private static final String HUOBI_API_URL = "https://api.huobi.pro/market/tickers";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public HuobiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetches prices for supported trading pairs from Huobi API
     * @return Map of symbol to [bidPrice, askPrice] values
     */
    public Map<String, BigDecimal[]> fetchPrices() {
        Map<String, BigDecimal[]> prices = new HashMap<>();

        try {
            // HTTP request to Huobi API
            String response = restTemplate.getForObject(HUOBI_API_URL, String.class);

            // Parse the response
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode dataArray = rootNode.get("data");

            // Process each symbol in the response
            for (JsonNode item : dataArray) {
                String symbol = item.get("symbol").asText().toUpperCase();

                // Get ETHUSDT and BTCUSDT bid price and ask price
                if ("ETHUSDT".equals(symbol) || "BTCUSDT".equals(symbol)) {
                    BigDecimal bidPrice = new BigDecimal(item.get("bid").asText()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal askPrice = new BigDecimal(item.get("ask").asText()).setScale(2, RoundingMode.HALF_UP);

                    // Store values in array [bidPrice, askPrice]
                    prices.put(symbol, new BigDecimal[]{bidPrice, askPrice});

                    logger.info("Huobi price for {}: Bid={}, Ask={}",
                            symbol, bidPrice, askPrice);
                }
            }
        } catch (JsonProcessingException e) {
            logger.error("Error parsing Huobi API response", e);
        } catch (Exception e) {
            logger.error("Error fetching prices from Huobi", e);
        }

        return prices;
    }

    //return enum value for Huobi
    public Exchange getExchangeName() {
        return Exchange.HUOBI;
    }
}