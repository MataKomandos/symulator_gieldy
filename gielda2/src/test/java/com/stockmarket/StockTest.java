package com.stockmarket;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla klasy Stock, weryfikujące enkapsulację, gettery oraz metody equals() i hashCode().
 */
public class StockTest {

    // Przykładowe obiekty testowe
    private final Stock stock1 = new Stock("CDR", "CD Projekt S.A.", 100.0);
    private final Stock stock2 = new Stock("CDR", "CD Projekt G.A.", 150.0); // Ten sam symbol, inne detale
    private final Stock stock3 = new Stock("TSLA", "Tesla Inc.", 800.0);    // Inny symbol

    @Test
    public void testStockCreationAndGetters() {
        // Weryfikacja poprawności tworzenia obiektu i działania getterów
        assertEquals("CDR", stock1.getSymbol(), "Symbol powinien być poprawnie ustawiony.");
        assertEquals("CD Projekt S.A.", stock1.getName(), "Nazwa powinna być poprawnie ustawiona.");
        assertEquals(100.0, stock1.getInitialPrice(), 0.001, "Cena początkowa powinna być poprawnie ustawiona.");
    }

    @Test
    public void testEquals_SameSymbolDifferentDetails() {
        // Weryfikacja, że akcje z tym samym symbolem są równe, niezależnie od ceny i nazwy
        assertTrue(stock1.equals(stock2), "Akcje o tym samym symbolu powinny być równe.");
    }

    @Test
    public void testEquals_DifferentSymbol() {
        // Weryfikacja nierówności dla różnych symboli
        assertFalse(stock1.equals(stock3), "Akcje o różnych symbolach nie powinny być równe.");
    }

    @Test
    public void testHashCode_Consistency() {
        // Weryfikacja, że obiekty równe mają ten sam hash code
        assertEquals(stock1.hashCode(), stock2.hashCode(), "Obiekty równe (ten sam symbol) muszą mieć ten sam hash code.");
    }

    @Test
    public void testHashCode_Difference() {
        // Weryfikacja, że obiekty różne mają zazwyczaj różne hash codes
        assertNotEquals(stock1.hashCode(), stock3.hashCode(), "Obiekty różne (różne symbole) powinny mieć różne hash codes.");
    }
}

