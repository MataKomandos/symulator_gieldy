package com.stockmarket.domain;

public class Commodity extends Asset {
    private final double baseStorageRate; // np. 0.001 (0.1%)

    public Commodity(String symbol, String name, double currentPrice, double baseStorageRate) {
        super(symbol, name, currentPrice);
        this.baseStorageRate = baseStorageRate;
    }

    @Override
    public double calculateRealValue(int quantity) {
        // Im więcej posiadasz, tym wyższy procentowy koszt magazynowania
        double storageCostFactor = baseStorageRate * quantity;
        double grossValue = currentPrice * quantity;
        return grossValue * (1.0 - storageCostFactor);
    }

    @Override
    public double calculateAcquisitionCost(int quantity) {
        return currentPrice * quantity;
    }
}
