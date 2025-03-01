package com.crypto.trading.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal balance;

    public Wallet(User user, String currencyCode, BigDecimal balance) {
        this.user = user;
        this.currencyCode = currencyCode;
        this.balance = balance;
    }
}