# Test Case

## 1. Create an api to retrieve the latest best aggregated price
Test data : http://localhost:8080/api/price/BTCUSDT  

## 2. Create an api which allow users to trade based on the latest best aggregated price
Test data : http://localhost:8080/api/trading/orders  
Body:
{
    "userID": 1,
    "tradingPair": "BTC/USDT", 
    "type": "SELL",
    "price": 85000,
    "quantity": 0.1
}

## 3. Create an api to retrieve the user's crypto currencies wallet balance  
Test data : http://localhost:8080/api/wallets/1

## 4. Create an api to retrieve the user trading history  
Test data : http://localhost:8080/api/trading/orders/1
