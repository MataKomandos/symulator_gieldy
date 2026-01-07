package com.stockmarket.logic;

import com.stockmarket.domain.Asset;
import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje portfel inwestycyjny, operujący polimorficznie na dowolnym typie aktywów (Asset).
 * Nie zawiera instrukcji if/instanceof sprawdzających typ aktywa.
 */
public class Portfolio {
    private double cash;
    private final List<AssetHolding> holdings = new ArrayList<>();

    // Wewnętrzna klasa pomocnicza do śledzenia posiadanych pozycji
    private static class AssetHolding {
        Asset asset;
        int quantity;

        AssetHolding(Asset asset, int quantity) {
            this.asset = asset;
            this.quantity = quantity;
        }
    }

    public Portfolio(double initialCash) {
        this.cash = initialCash;
    }

    public void buyAsset(Asset asset, int quantity) {
        // POPRAWKA: Ignorujemy operację, jeśli ilość jest niepoprawna lub zerowa
        if (quantity <= 0) {
            return;
        }

        double totalCost = asset.calculateAcquisitionCost(quantity);

        if (totalCost > cash) {
            throw new IllegalArgumentException("Insufficient funds! Required: " + totalCost + ", Available: " + cash);
        }

        cash -= totalCost;
        updateHoldings(asset, quantity);
    }

    private void updateHoldings(Asset asset, int quantity) {
        for (AssetHolding holding : holdings) {
            if (holding.asset.equals(asset)) {
                holding.quantity += quantity;
                return;
            }
        }
        holdings.add(new AssetHolding(asset, quantity));
    }

    public double calculateTotalNetWorth() {
        double totalAssetsValue = 0;
        for (AssetHolding holding : holdings) {
            // KLUCZ: Polimorficzne wywołanie metody calculateRealValue
            totalAssetsValue += holding.asset.calculateRealValue(holding.quantity);
        }
        return cash + totalAssetsValue;
    }

    public double getCash() { return cash; }

    /**
     * Zwraca liczbę różnych typów aktywów (unikalnych symboli) w portfelu.
     */
    public int getHoldingsCount() {
        return holdings.size();
    }
}
