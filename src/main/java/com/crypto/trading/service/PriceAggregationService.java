package com.crypto.trading.service;


import com.crypto.trading.model.PriceAggregation;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.model.enums.CurrencyPair;
import com.crypto.trading.model.enums.Exchange;
import com.crypto.trading.repository.PriceAggregationRepository;
import com.crypto.trading.service.ExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceAggregationService {
    private static final Logger logger = LoggerFactory.getLogger(PriceAggregationService.class);

    private final List<ExchangeService> exchangeServices;
    private final PriceAggregationRepository priceAggregationRepository;

    public PriceAggregation findLatestPriceBySymbol(String symbol) {
        return priceAggregationRepository.findTopBySymbolOrderByTimestampDesc(symbol);
    }

    public PriceAggregationService(List<ExchangeService> exchangeServices,
                                   PriceAggregationRepository priceAggregationRepository) {
        this.exchangeServices = exchangeServices;
        this.priceAggregationRepository = priceAggregationRepository;
    }


    //Fetch and aggregate prices every 10 seconds
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void PriceAggregation() {
        logger.info("Starting price aggregation...");

        // Initialize maps to track best prices for each symbol
        Map<String, BigDecimal> bestBidPrices = new HashMap<>();
        Map<String, String> bestBidExchanges = new HashMap<>();
        Map<String, BigDecimal> bestAskPrices = new HashMap<>();
        Map<String, String> bestAskExchanges = new HashMap<>();

        // Initialize with the first supported currency pairs
        for (CurrencyPair pair : CurrencyPair.values()) {
            bestBidPrices.put(pair.name(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            bestAskPrices.put(pair.name(), BigDecimal.valueOf(Double.parseDouble("100000000")).setScale(2, RoundingMode.HALF_UP));
        }

        // Fetch prices from all exchanges
        for (ExchangeService exchangeService : exchangeServices) {
            try {
                Map<String, BigDecimal[]> prices = exchangeService.fetchPrices();
                String exchangeName = exchangeService.getExchangeName().name();

                // Compare and update best prices
                for (Map.Entry<String, BigDecimal[]> entry : prices.entrySet()) {
                    String symbol = entry.getKey();
                    BigDecimal bidPrice = entry.getValue()[0];
                    BigDecimal askPrice = entry.getValue()[1];

                    logger.info("bidPrice : " + String.valueOf(bidPrice));
                    logger.info("askprice : " + String.valueOf(askPrice));
                    logger.info("bestbidPrice : " + String.valueOf(bestBidPrices));
                    logger.info("bestAskPrices : " + String.valueOf(bestAskPrices));
                    // Update best bid price (highest)
                    if (bidPrice.compareTo(bestBidPrices.get(symbol)) > 0) {
                        bestBidPrices.put(symbol, bidPrice);
                        bestBidExchanges.put(symbol, exchangeName);
                    }

                    // Update best ask price (lowest)
                    if (askPrice.compareTo(bestAskPrices.get(symbol)) < 0) {
                        bestAskPrices.put(symbol, askPrice);
                        bestAskExchanges.put(symbol, exchangeName);
                    }
                }
            } catch (Exception e) {
                logger.error("Error fetching prices from {}",
                        exchangeService.getExchangeName(), e);
            }
        }

        // Save aggregated prices to database
        LocalDateTime now = LocalDateTime.now();
        for (CurrencyPair pair : CurrencyPair.values()) {
            String symbol = pair.name();

            // Skip invalid or missing prices
            if (bestBidPrices.get(symbol).equals(BigDecimal.ZERO) ||
                    bestAskPrices.get(symbol).equals(BigDecimal.valueOf(Double.MAX_VALUE))) {
                logger.warn("Skipping {} due to missing valid prices", symbol);
                continue;
            }

            // Create and save the aggregated price
            PriceAggregation aggregatedPrice = new PriceAggregation(
                    symbol,
                    bestBidPrices.get(symbol),
                    bestAskPrices.get(symbol),
                    bestBidExchanges.get(symbol),
                    bestAskExchanges.get(symbol),
                    now
            );

            priceAggregationRepository.save(aggregatedPrice);

            logger.info("Saved aggregated price for {}: Bid={} ({}), Ask={} ({})",
                    symbol,
                    aggregatedPrice.getBidPrice(), aggregatedPrice.getBidExchange(),
                    aggregatedPrice.getAskPrice(), aggregatedPrice.getAskExchange());
        }
    }

    public PriceAggregation getLatestPrice(String symbol) {
        return priceAggregationRepository.findTopBySymbolOrderByTimestampDesc(symbol);
    }

    public Map<String, PriceAggregation> getAllLatestPrices() {
        Map<String, PriceAggregation> latestPrices = new HashMap<>();

        for (CurrencyPair pair : CurrencyPair.values()) {
            PriceAggregation price = getLatestPrice(pair.name());
            if (price != null) {
                latestPrices.put(pair.name(), price);
            }
        }

        return latestPrices;
    }
}