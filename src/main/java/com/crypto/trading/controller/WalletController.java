package com.crypto.trading.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.service.WalletService;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    //API for wallet balance
    @GetMapping("/{userId}")
    public ResponseEntity<List<Wallet>> getUserWalletsById(@PathVariable Long userId) {
        List<Wallet> wallets = walletService.getUserWalletsById(userId);
        return ResponseEntity.ok(wallets);
    }
}
