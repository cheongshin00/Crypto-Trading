package com.crypto.trading.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_order_id", nullable = false)
    private Order buyOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sell_order_id", nullable = false)
    private Order sellOrder;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal price;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal quantity;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        executedAt = LocalDateTime.now();
    }
}
