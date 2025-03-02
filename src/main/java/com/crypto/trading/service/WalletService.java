package com.crypto.trading.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.repository.WalletRepository;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public List<Wallet> getUserWalletsById(Long userID) {
        return walletRepository.findByUserId(userID);
    }

    //Update wallet balance
    @Transactional
    public void updateBalance(User user, String currencyCode, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserAndCurrencyCode(user, currencyCode);

        if (wallet == null) {
            wallet = new Wallet(user, currencyCode, amount);
            System.out.println("Creating new wallet for " + user.getUsername() + ": " + currencyCode + " = " + amount);

        } else {
            wallet.setBalance(wallet.getBalance().add(amount));
            System.out.println("Updating balance for " + user.getUsername() + ": " + currencyCode +
                    " (Old: " + wallet.getBalance() + ", Change: " + amount + ", New: " + wallet.getBalance().add(amount) + ")");
        }

        walletRepository.save(wallet);
    }

    //Verify if the wallet has sufficient balance
    @Transactional(readOnly = true)
    public boolean hasSufficientBalance(User user, String currencyCode, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserAndCurrencyCode(user, currencyCode);
        return wallet != null && wallet.getBalance().compareTo(amount) >= 0;
    }
}