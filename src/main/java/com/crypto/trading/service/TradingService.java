package com.crypto.trading.service;

import java.math.BigDecimal;
import java.util.List;

import com.crypto.trading.model.*;
import com.crypto.trading.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crypto.trading.dto.OrderDTO;
import com.crypto.trading.model.enums.OrderStatus;
import com.crypto.trading.model.enums.OrderType;

@Service
public class TradingService {
    private static final Logger logger = LoggerFactory.getLogger(TradingService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PriceAggregationRepository priceAggregationRepository;


    @Autowired
    private TradingPairRepository tradingPairRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletService walletService;

    @Transactional
    public Order placeOrder(OrderDTO orderDTO) {
        User user = getUser(orderDTO.getUserID());

        String[] symbols = orderDTO.getTradingPair().split("/");
        String baseCurrency = symbols[0];
        String quoteCurrency = symbols[1];

        TradingPair tradingPair = tradingPairRepository.findByBaseCurrencyAndQuoteCurrency(baseCurrency, quoteCurrency);

        if (tradingPair == null || !tradingPair.isActive()) {
            throw new IllegalArgumentException("Trading pair not supported: " + orderDTO.getTradingPair());
        }

        OrderType orderType = OrderType.valueOf(orderDTO.getType());

        // Verify sufficient funds
        if (orderType == OrderType.BUY) {
            BigDecimal totalCost = orderDTO.getPrice().multiply(orderDTO.getQuantity());
            if (!walletService.hasSufficientBalance(user, quoteCurrency, totalCost)) {
                throw new IllegalStateException("Insufficient " + quoteCurrency + " balance");
            }

            // Reserve the USDT
            walletService.updateBalance(user, quoteCurrency, totalCost.negate());
        } else {
            if (!walletService.hasSufficientBalance(user, baseCurrency, orderDTO.getQuantity())) {
                throw new IllegalStateException("Insufficient " + baseCurrency + " balance");
            }

            // Reserve the BTC/ETH
            walletService.updateBalance(user, baseCurrency, orderDTO.getQuantity().negate());
        }

        // Create the order
        Order order = new Order();
        order.setUser(user);
        order.setTradingPair(tradingPair);
        order.setType(orderType);
        order.setPrice(orderDTO.getPrice());
        order.setQuantity(orderDTO.getQuantity());
        order.setRemainingQuantity(orderDTO.getQuantity());
        order.setStatus(OrderStatus.OPEN);

        order = orderRepository.save(order);

        return order;
    }

    //Verify if the order hit the market price every 5 seconds
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void processOrders() {
        logger.info("Starting order execution...");
        List<Order> openOrders = orderRepository.findByStatus(OrderStatus.OPEN);
        if (openOrders.isEmpty()) {
            logger.info("No order...");
            return;
        }

        for (Order order : openOrders) {
            logger.info("Have order...");
            matchOrder(order);
        }
    }

    @Transactional
    public void matchOrder(Order order) {
        TradingPair tradingPair = order.getTradingPair();
        BigDecimal latestAskPrice = getLatestAggregatedPrice(String.valueOf(tradingPair.getPairSymbol())).getAskPrice();
        BigDecimal latestBidPrice = getLatestAggregatedPrice(String.valueOf(tradingPair.getPairSymbol())).getBidPrice();

        if (latestAskPrice == null || latestBidPrice == null) {
            return; // No market data available
        }

        boolean canExecute = (order.getType() == OrderType.BUY && latestAskPrice.compareTo(order.getPrice()) <= 0) ||
                (order.getType() == OrderType.SELL && latestBidPrice.compareTo(order.getPrice()) >= 0);

        if (!canExecute) {
            return;
        }

        BigDecimal executionPrice = (order.getType() == OrderType.BUY) ? latestAskPrice : latestBidPrice;
        BigDecimal matchedQuantity = order.getRemainingQuantity();
        order.setRemainingQuantity(BigDecimal.ZERO);
        order.setStatus(OrderStatus.FILLED);


        // Create a transaction
        Transaction transaction = new Transaction();
        transaction.setOrder(order);
        transaction.setPrice(executionPrice);
        transaction.setQuantity(matchedQuantity);

        // Save the transaction
        transactionRepository.save(transaction);

        // Process the transaction
        settleTransaction(transaction);

        // Save the updated orders
        orderRepository.save(order);

    }


    @Transactional
    private void settleTransaction(Transaction transaction) {
        Order order = transaction.getOrder();
        User user = order.getUser();

        String baseCurrency = order.getTradingPair().getBaseCurrency();
        String quoteCurrency = order.getTradingPair().getQuoteCurrency();

        BigDecimal quantity = transaction.getQuantity();
        BigDecimal totalCost = transaction.getPrice().multiply(quantity);

        if (order.getType() == OrderType.BUY) {
            walletService.updateBalance(user, baseCurrency, quantity);  // Add BTC/ETH

        } else if (order.getType() == OrderType.SELL) {
            walletService.updateBalance(user, quoteCurrency, totalCost);  // Add USDT
        } else {
            throw new IllegalStateException("Unknown order type.");
        }

    }

    public List<Order> getUserOrders(Long userID) {
        return orderRepository.findByUserId(userID);
    }

    public List<Transaction> getUserTransactions(Long userID) {
        return transactionRepository.findByUserId(userID);
    }

    public User getUser(Long userID) {
        return userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userID));
    }

    private PriceAggregation getLatestAggregatedPrice(String symbol) {
        return priceAggregationRepository.findTopBySymbolOrderByTimestampDesc(symbol);
    }

}