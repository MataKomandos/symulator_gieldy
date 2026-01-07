package com.stockmarket.domain;

public class Currency extends Asset {
    private final double spread; // Różnica między ceną rynkową a ceną sprzedaży (Bid)

    public Currency(String symbol, String name, double currentPrice, double spread) {
        super(symbol, name, currentPrice);
        this.spread = spread;
    }

    @Override
    public double calculateRealValue(int quantity) {
        // Realna wartość to cena "Bid" (rynkowa - spread)
        return (currentPrice - spread) * quantity;
    }

    @Override
    public double calculateAcquisitionCost(int quantity) {
        return currentPrice * quantity;
    }
}