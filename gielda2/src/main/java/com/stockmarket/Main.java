package com.stockmarket;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.Commodity;
import com.stockmarket.domain.Currency;
import com.stockmarket.domain.Share;
import com.stockmarket.logic.Portfolio;

/**
 * Krótki pokaz działania portfela: ten sam kod kupuje akcje, surowiec i walutę,
 * a każdy rodzaj aktywa sam wie, ile kosztuje zakup i ile naprawdę jest wart.
 */
public class Main {

    public static void main(String[] args) {
        Portfolio portfolio = new Portfolio(20_000.0);

        Asset share = new Share("CDR", "CD Projekt", 100.0);
        Asset gold = new Commodity("XAU", "Gold", 250.0, 0.001);
        Asset euro = new Currency("EUR", "Euro", 4.30, 0.05);

        buy(portfolio, share, 20);
        buy(portfolio, gold, 10);
        buy(portfolio, euro, 1_000);

        System.out.println();
        System.out.printf("Holdings:   %d%n", portfolio.getHoldingsCount());
        System.out.printf("Cash:       %10.2f PLN%n", portfolio.getCash());
        System.out.printf("Net worth:  %10.2f PLN%n", portfolio.calculateTotalNetWorth());
        System.out.println("Net worth is below the 20000 PLN we started with because every market has its costs:");
        System.out.println("a flat share fee, commodity storage and the currency spread.");

        System.out.println();
        System.out.println("Buying more than the cash allows:");
        try {
            portfolio.buyAsset(share, 1_000);
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }
    }

    private static void buy(Portfolio portfolio, Asset asset, int quantity) {
        double cost = asset.calculateAcquisitionCost(quantity);
        double value = asset.calculateRealValue(quantity);
        portfolio.buyAsset(asset, quantity);
        System.out.printf("Bought %5d x %-4s cost %9.2f PLN, real value %9.2f PLN%n",
                quantity, asset.getSymbol(), cost, value);
    }
}
