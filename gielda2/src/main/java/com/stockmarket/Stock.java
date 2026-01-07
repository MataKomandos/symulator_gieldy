package com.stockmarket;

import java.util.Objects;

/**
 * Reprezentuje pojedynczą akcję z unikalnym symbolem, nazwą firmy i ceną początkową.
 * Akcje są uważane za identyczne, jeśli mają ten sam symbol.
 */
public class Stock {
    private final String symbol;
    private final String name;
    private final double initialPrice;

    /**
     * Konstruktor klasy Stock.
     *
     * @param symbol Unikalny symbol giełdowy (np. "CDR").
     * @param name Pełna nazwa firmy (np. "CD Projekt S.A.").
     * @param initialPrice Cena początkowa akcji.
     */
    public Stock(String symbol, String name, double initialPrice) {
        this.symbol = symbol;
        this.name = name;
        this.initialPrice = initialPrice;
    }

    // --- Metody dostępowe (Gettery) ---

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    // --- Metody equals() i hashCode() (Równość na podstawie symbolu) ---

    /**
     * Dwa obiekty Stock są równe, jeśli ich symbole są równe.
     * @param o Obiekt do porównania.
     * @return True, jeśli symbole pasują, false w przeciwnym razie.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stock stock = (Stock) o;
        return Objects.equals(symbol, stock.symbol);
    }

    /**
     * Hash code jest generowany wyłącznie na podstawie symbolu akcji.
     * @return Wartość hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
