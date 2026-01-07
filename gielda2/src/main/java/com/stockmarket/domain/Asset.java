package com.stockmarket.domain;

import java.util.Objects;

public abstract class Asset {
    private final String symbol;
    private final String name;
    protected double currentPrice;

    public Asset(String symbol, String name, double currentPrice) {
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = currentPrice;
    }

    public String getSymbol() { return symbol; }
    public double getCurrentPrice() { return currentPrice; }

    /**
     * Oblicza realną wartość posiadanych aktywów z uwzględnieniem kosztów specyficznych dla rynku.
     */
    public abstract double calculateRealValue(int quantity);

    /**
     * Oblicza całkowity koszt zakupu (cena rynkowa + opłaty wejściowe).
     */
    public abstract double calculateAcquisitionCost(int quantity);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(symbol, asset.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}

