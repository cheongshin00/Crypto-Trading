package com.crypto.trading.controller;

import java.util.List;

import com.crypto.trading.model.PriceAggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.service.PriceAggregationService;

@RestController
@RequestMapping("/api/price")
public class PriceAggregationController {

    @Autowired
    private PriceAggregationService priceAggregationService;

    //Get latest price by symbol
    @GetMapping("/{symbol}")
    public ResponseEntity<PriceAggregation> findLatestPriceBySymbol(@PathVariable String symbol) {
        PriceAggregation priceAggregation = priceAggregationService.findLatestPriceBySymbol(symbol);
        return ResponseEntity.ok(priceAggregation);
    }
}
