package com.crypto.trading.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Wallet findByUserAndCurrencyCode(User user, String currencyCode);

    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    List<Wallet> findByUserId(@Param("userId") Long userId);
}