package com.crypto.trading.controller;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.crypto.trading.dto.OrderDTO;
import com.crypto.trading.model.Order;
import com.crypto.trading.model.Transaction;
import com.crypto.trading.model.User;
import com.crypto.trading.service.TradingService;

@RestController
@RequestMapping("/api/trading")
public class TradingController {

    @Autowired
    private TradingService tradingService;

    //Place order
    @PostMapping("/orders")
    public ResponseEntity<Order> placeOrder(@RequestBody OrderDTO orderDTO ) {
        Order order = tradingService.placeOrder(orderDTO);
        return ResponseEntity.ok(order);
    }

    //Get user order
    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = tradingService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    //Get user transaction
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getUserTransactions(@PathVariable Long userId) {
        List<Transaction> transactions = tradingService.getUserTransactions(userId);
        return ResponseEntity.ok(transactions);
    }
}