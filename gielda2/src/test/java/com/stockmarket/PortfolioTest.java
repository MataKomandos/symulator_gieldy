package com.stockmarket;

import com.stockmarket.domain.Asset;
import com.stockmarket.domain.Share;
import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testy jednostkowe dla klasy Portfolio, weryfikujące inicjalizację, dodawanie aktywów i obliczenia wartości.
 * Testuje zachowanie klasy Share (następcy Stock).
 */
public class PortfolioTest {

    private Portfolio portfolio;

    // Przykładowe Aktywa (Share to teraz następca Stock)
    private final Share cdrShare = new Share("CDR", "CD Projekt", 100.0);
    private final Share tslaShare = new Share("TSLA", "Tesla", 800.0);
    // Aktywa z tym samym symbolem powinny być równe
    private final Share cdrShare2 = new Share("CDR", "CD Projekt SA", 120.0);
    // UWAGA: Testy muszą używać tych samych aktywów (symboli), co w nowej logice (Share.TRANSACTION_FEE = 5.0)

    @BeforeEach
    void setUp() {
        // Inicjalizacja nowego portfela przed każdym testem
        portfolio = new Portfolio(10000.0);
    }

    @Test
    void testPortfolioInitialization_EmptyState() {
        // Sprawdzenie początkowego stanu portfela
        assertEquals(10000.0, portfolio.getCash(), 0.001, "Początkowa gotówka powinna być poprawna.");
        assertEquals(0, portfolio.getHoldingsCount(), "Początkowa liczba pozycji aktywów powinna wynosić 0.");

        // Nowa metoda oblicza całość majątku (Gotówka + Aktywa)
        assertEquals(10000.0, portfolio.calculateTotalNetWorth(), 0.001, "Początkowa wartość portfela powinna być równa gotówce.");
    }

    @Test
    void testBuyAsset_NewHolding() {
        // Cena: 5 * 100.0 = 500.0 + 5.0 opłaty = 505.0 koszt
        portfolio.buyAsset(cdrShare, 5); // Zastępuje addStock

        // Oczekiwana Gotówka: 10000.0 - 505.0 = 9495.0
        double expectedCash = 9495.0;
        // Oczekiwana Wartość Rzeczywista Aktywów: 5 * 100.0 = 500.0
        double expectedNetWorth = expectedCash + 500.0;

        assertEquals(1, portfolio.getHoldingsCount(), "Powinna być 1 unikalna pozycja.");
        assertEquals(expectedNetWorth, portfolio.calculateTotalNetWorth(), 0.001, "Wartość majątku powinna uwzględniać zakup.");

        // UWAGA: Nie możemy już użyć getStockQuantity, bo nie ma tej metody w nowym Portfolio.
        // Jeśli MUSISZ testować ilość, musiałbyś dodać publiczną metodę w Portfolio do tego celu.
    }

    @Test
    void testBuyAsset_UpdateExistingHolding() {
        // Koszt pierwszego zakupu (5 * 100) + 5 = 505
        portfolio.buyAsset(cdrShare, 5);
        // Koszt drugiego zakupu (10 * 100) + 5 = 1005
        portfolio.buyAsset(cdrShare, 10);
        // Koszt trzeciego zakupu (3 * 120) + 5 = 365 (Używamy CDR2, ale symbol jest ten sam!)
        portfolio.buyAsset(cdrShare2, 3);

        assertEquals(1, portfolio.getHoldingsCount(), "Liczba unikalnych pozycji powinna pozostać 1.");

        // Suma kosztów: 505 + 1005 + 365 = 1875
        // Gotówka: 10000 - 1875 = 8125.0
        // Ilość Aktywów: 5 + 10 + 3 = 18
        // Wartość Rzeczywista (bazuje na ostatniej cenie/ostatnim obiekcie CDR): 18 * 120.0 = 2160.0 (lub na pierwszym: 18 * 100.0)
        // Ze względu na nieścisłości w oryginalnej implementacji (która cena jest używana w StockHolding),
        // testujemy tylko ogólną wartość majątku. Użyjemy ceny początkowej 100.0, która była pierwsza.

        // Wartość Rzeczywista: 18 * 100.0 = 1800.0
        double expectedNetWorth = 8125.0 + 1800.0;
        assertEquals(expectedNetWorth, portfolio.calculateTotalNetWorth(), 0.001, "Wartość majątku powinna być poprawnie zsumowana.");
    }

    // Dodatkowy test weryfikujący logikę zakupu (wymagań z Etapu 2)
    @Test
    void testBuyAsset_InsufficientFundsWithFee() {
        Portfolio poorPortfolio = new Portfolio(104.0);
        // Koszt: 1 * 100.0 (cena) + 5.0 (opłata manipulacyjna) = 105.0. Brakuje 1.0.
        assertThrows(IllegalArgumentException.class, () -> {
            poorPortfolio.buyAsset(cdrShare, 1);
        }, "Zakup powinien zakończyć się wyjątkiem z powodu braku środków na opłatę.");
    }
}
