package com.stockmarket;

import com.stockmarket.domain.*;
import com.stockmarket.logic.Portfolio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PortfolioLogicTest {

    private Portfolio portfolio;

    // Aktywa dla testów polimorfizmu i logiki
    private final Share share;
    private final Commodity commodity;
    private final Currency currency;
    private final Share cheapShare;
    private final Commodity expensiveCommodity;

    public PortfolioLogicTest() {
        // Wszystkie aktywa mają cenę bazową 100.0 dla łatwej porównywalności
        share = new Share("AAPL", "Apple", 100.0);
        commodity = new Commodity("GOLD", "Gold", 100.0, 0.01); // 1% kosztu na jednostkę
        currency = new Currency("EUR", "Euro", 100.0, 2.0);      // 2.0 spreadu
        cheapShare = new Share("G", "Groszowa", 1.0);
        expensiveCommodity = new Commodity("OIL", "Ropa", 1000.0, 0.005);
    }

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio(4000.0);
    }

    // ######################################################################
    // 1. TESTY LOGIKI AKTYWÓW (UNIT TESTS DLA KLAS DOMAIN)
    // ######################################################################

    // --- Share (Akcje) ---

    @Test
    void shareAcquisitionCostShouldIncludeFixedFee() {
        // 10 * 100.0 + 5.0 (fee) = 1005.0
        assertEquals(1005.0, share.calculateAcquisitionCost(10), 0.001);
    }

    @Test
    void shareAcquisitionCostShouldShowFeeDominanceForSmallBuys() {
        // 1 * 1.0 + 5.0 (fee) = 6.0
        assertEquals(6.0, cheapShare.calculateAcquisitionCost(1), 0.001);
    }

    @Test
    void shareRealValueShouldBePurePrice() {
        // 10 * 100.0 = 1000.0
        assertEquals(1000.0, share.calculateRealValue(10), 0.001);
    }

    // --- Commodity (Surowce) ---

    @Test
    void commodityRealValueShouldIncludeStorageLoss() {
        // Surowiec: (10 * 100.0) * (1 - 0.01 * 10) = 1000 * 0.9 = 900.0
        assertEquals(900.0, commodity.calculateRealValue(10), 0.001);
    }

    @Test
    void commodityStorageCostShouldScaleWithVolume() {
        // Wartość 10 jednostek: 900.0 / 10 = 90.0 na jednostkę
        double valuePerUnitSmall = commodity.calculateRealValue(10) / 10.0;

        // Wartość 50 jednostek: 50 * 100 * (1 - 0.01 * 50) = 5000 * 0.5 = 2500.0
        // Wartość na jednostkę: 2500.0 / 50 = 50.0 na jednostkę
        double valuePerUnitLarge = commodity.calculateRealValue(50) / 50.0;

        // Wartość jednostkowa musi spaść: 90.0 > 50.0
        assertTrue(valuePerUnitLarge < valuePerUnitSmall, "Większy wolumen powinien generować wyższy koszt procentowy.");
    }

    @Test
    void commodityAcquisitionCostShouldBePurePrice() {
        // 10 * 100.0 = 1000.0 (Brak opłat wejściowych)
        assertEquals(1000.0, commodity.calculateAcquisitionCost(10), 0.001);
    }

    // --- Currency (Waluty) ---

    @Test
    void currencyRealValueShouldIncludeSpreadLoss() {
        // Waluta: (100.0 - 2.0) * 10 = 98 * 10 = 980.0
        assertEquals(980.0, currency.calculateRealValue(10), 0.001);
    }

    @Test
    void currencyValueIsAlwaysLowerThanMarketPriceDueToSpread() {
        // Realna kwota (Bid Price) musi być niższa niż cena rynkowa (100.0)
        assertTrue(currency.calculateRealValue(1) < 100.0);
    }

    @Test
    void currencyValueShouldBeZeroIfPriceEqualsSpread() {
        // Edge Case: Cena 100.0, Spread 100.0
        Currency zeroValueCurrency = new Currency("ZVC", "ZeroValue", 100.0, 100.0);
        assertEquals(0.0, zeroValueCurrency.calculateRealValue(10), 0.001);
    }

    // ######################################################################
    // 2. TESTY POLIMORFIZMU W PORTFOLIO
    // ######################################################################

    @Test
    void testTotalNetWorthPolymorphism() {
        // Zakup: Share (koszt 1005), Commodity (koszt 1000), Currency (koszt 1000)
        portfolio.buyAsset(share, 10);
        portfolio.buyAsset(commodity, 10);
        portfolio.buyAsset(currency, 10);

        // Całkowity Koszt Zakupów = 3005.0
        // Gotówka po zakupach = 4000.0 - 3005.0 = 995.0

        // Wartość Aktywów = 1000.0 + 900.0 + 980.0 = 2880.0

        // Całkowita Wartość Portfela = 995.0 + 2880.0 = 3875.0
        double expectedNetWorth = 995.0 + 1000.0 + 900.0 + 980.0;

        // Weryfikacja, czy Portfolio poprawnie wywołało różne metody calculateRealValue()
        assertEquals(3875.0, portfolio.calculateTotalNetWorth(), 0.001);
    }

    @Test
    void testGetHoldingsCount_MultipleTypes() {
        portfolio.buyAsset(share, 1);
        portfolio.buyAsset(commodity, 1);
        portfolio.buyAsset(currency, 1);

        assertEquals(3, portfolio.getHoldingsCount());
    }

    @Test
    void testGetHoldingsCount_SameTypeMultipleBuys() {
        portfolio.buyAsset(share, 1);
        portfolio.buyAsset(share, 5); // To samo aktywo, powinna być 1 pozycja

        assertEquals(1, portfolio.getHoldingsCount());
    }

    // ######################################################################
    // 3. TESTY WYJĄTKÓW I BRZEGOWYCH SYTUACJI (ERROR HANDLING)
    // ######################################################################

    @Test
    void testPurchaseShouldFailWhenNotEnoughMoneyForShareFee() {
        Portfolio poorPortfolio = new Portfolio(104.0);
        // Koszt: 1 * 100.0 + 5.0 fee = 105.0. Brakuje 1.0.
        assertThrows(IllegalArgumentException.class, () -> {
            poorPortfolio.buyAsset(share, 1);
        }, "Zakup Share musi uwzględniać stałą opłatę.");
    }

    @Test
    void testPurchaseShouldFailWhenNotEnoughMoneyForExpensiveCommodity() {
        // Cena: 10 * 1000.0 = 10000.0. Posiadamy 4000.0.
        assertThrows(IllegalArgumentException.class, () -> {
            portfolio.buyAsset(expensiveCommodity, 10);
        }, "Zakup Commodity powinien się nie powieść z powodu zbyt wysokiej ceny.");
    }

    @Test
    void testZeroQuantityPurchase() {
        // Próba zakupu zerowej ilości nie powinna zmieniać stanu konta.
        portfolio.buyAsset(share, 0);
        assertEquals(0, portfolio.getHoldingsCount());
    }

    @Test
    void testBuyAssetUpdatesCashCorrectly() {
        // Koszt waluty: 10 * 100.0 = 1000.0
        portfolio.buyAsset(currency, 10);

        assertEquals(3000.0, portfolio.getCash(), 0.001);
    }
}