package com.crypto.trading;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.crypto.trading.model.TradingPair;
import com.crypto.trading.repository.TradingPairRepository;
import com.crypto.trading.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.repository.UserRepository;
import com.crypto.trading.service.WalletService;

@SpringBootApplication
public class TradingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingApplication.class, args);
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private TradingPairRepository tradingPairRepository;

	@Autowired
	private WalletService walletService;

	@Bean
	public CommandLineRunner initData() {
		return args -> {
			// Create test user
			User user = new User();
			user.setUsername("testuser");
			userRepository.save(user);

			// Initialize wallet with 50,000 USDT
			Wallet wallet = new Wallet(user, "USDT", new BigDecimal("50000.00").setScale(2, RoundingMode.HALF_UP));
			walletRepository.save(wallet);

			// Add supported trading pairs
			TradingPair ethUsdt = new TradingPair();
			ethUsdt.setBaseCurrency("ETH");
			ethUsdt.setQuoteCurrency("USDT");
			tradingPairRepository.save(ethUsdt);

			TradingPair btcUsdt = new TradingPair();
			btcUsdt.setBaseCurrency("BTC");
			btcUsdt.setQuoteCurrency("USDT");
			tradingPairRepository.save(btcUsdt);
		};
	}
}
