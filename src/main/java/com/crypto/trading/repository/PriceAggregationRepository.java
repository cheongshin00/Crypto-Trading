package com.crypto.trading.repository;

import com.crypto.trading.model.PriceAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceAggregationRepository extends JpaRepository<PriceAggregation, Long> {

    PriceAggregation findTopBySymbolOrderByTimestampDesc(String symbol);
}