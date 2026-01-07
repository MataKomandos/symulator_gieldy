package com.stockmarket.domain;

public class Share extends Asset {
    private static final double TRANSACTION_FEE = 5.0; // Stała opłata manipulacyjna

    public Share(String symbol, String name, double currentPrice) {
        super(symbol, name, currentPrice);
    }

    @Override
    public double calculateRealValue(int quantity) {
        // Wartość akcji to po prostu cena rynkowa (opłata była przy zakupie)
        return currentPrice * quantity;
    }

    @Override
    public double calculateAcquisitionCost(int quantity) {
        // Koszt zakupu to cena + stała opłata
        return (currentPrice * quantity) + TRANSACTION_FEE;
    }
}
