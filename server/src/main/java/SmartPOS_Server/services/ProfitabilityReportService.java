
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.ProfitabilityReportDTO;
import SmartPOS_Server.interfaces.IProfitabilityReportService;
import SmartPOS_Server.models.Sale;
import SmartPOS_Server.models.SaleItem;
import SmartPOS_Server.models.SaleReturn;
import SmartPOS_Server.models.SaleReturnItem;
import SmartPOS_Server.repositories.SaleRepository;
import SmartPOS_Server.repositories.SaleReturnRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ProfitabilityReportService
        implements IProfitabilityReportService {

    private final SaleRepository saleRepository;
    private final SaleReturnRepository saleReturnRepository;

    public ProfitabilityReportService(
            SaleRepository saleRepository,
            SaleReturnRepository saleReturnRepository
    ) {
        this.saleRepository = saleRepository;
        this.saleReturnRepository = saleReturnRepository;
    }

    @Override
    public ProfitabilityReportDTO getProfitabilityReport(
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "יש להזין תאריך התחלה ותאריך סיום"
            );
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "תאריך הסיום לא יכול להיות לפני תאריך ההתחלה"
            );
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;

        BigDecimal costOfGoodsSold = BigDecimal.ZERO;
        BigDecimal returnedGoodsCost = BigDecimal.ZERO;

        long totalSales = 0;
        int salesWithMissingCost = 0;

        boolean allCostsAvailable = true;

        Map<String, ProductAccumulator> products =
                new LinkedHashMap<>();

        // שליפת המכירות וסינון לפי טווח התאריכים
        List<Sale> sales = saleRepository.findAll()
                .stream()
                .filter(sale -> sale.getCreatedAt() != null)
                .filter(sale ->
                        !sale.getCreatedAt().isBefore(start)
                                && sale.getCreatedAt().isBefore(end)
                )
                .filter(sale ->
                        sale.getSaleStatus() == null
                                || "COMPLETED".equals(
                                sale.getSaleStatus()
                        )
                )
                .toList();

        for (Sale sale : sales) {

            totalSales++;

            BigDecimal saleTotal = safe(sale.getTotal());
            BigDecimal saleSubtotal = safe(sale.getSubtotal());

            totalRevenue = totalRevenue.add(saleTotal);

            // חישוב יחס ההנחה של המכירה
            BigDecimal discountRatio = BigDecimal.ONE;

            if (saleSubtotal.compareTo(BigDecimal.ZERO) > 0) {
                discountRatio = saleTotal.divide(
                        saleSubtotal,
                        10,
                        RoundingMode.HALF_UP
                );
            }

            boolean saleHasMissingCost = false;

            // עיבוד פריטי המכירה
            if (sale.getItems() != null) {

                for (SaleItem item : sale.getItems()) {

                    String key = createProductKey(
                            item.getProductId(),
                            item.getSku()
                    );

                    ProductAccumulator product =
                            products.computeIfAbsent(
                                    key,
                                    ignored -> new ProductAccumulator(
                                            item.getProductId(),
                                            item.getProductName(),
                                            item.getSku()
                                    )
                            );

                    int quantity = item.getQuantity();

                    product.quantitySold += quantity;

                    // הכנסה לאחר חלוקת ההנחה
                    BigDecimal itemRevenue =
                            safe(item.getTotalPrice())
                                    .multiply(discountRatio);

                    product.revenue =
                            product.revenue.add(itemRevenue);

                    // עלות הקנייה ההיסטורית
                    BigDecimal purchasePrice =
                            item.getPurchasePriceAtSale();

                    if (purchasePrice == null) {

                        saleHasMissingCost = true;
                        allCostsAvailable = false;
                        product.missingCost = true;

                    } else {

                        BigDecimal itemCost =
                                purchasePrice.multiply(
                                        BigDecimal.valueOf(quantity)
                                );

                        costOfGoodsSold =
                                costOfGoodsSold.add(itemCost);

                        product.cost =
                                product.cost.add(itemCost);
                    }
                }
            }

            // שליפת ההחזרות של המכירה
            List<SaleReturn> returns =
                    saleReturnRepository.findBySaleId(sale.getId());

            if (returns == null) {
                returns = Collections.emptyList();
            }

            for (SaleReturn saleReturn : returns) {

                totalRefunds = totalRefunds.add(
                        safe(saleReturn.getRefundAmount())
                );

                if (saleReturn.getItems() == null) {
                    continue;
                }

                for (SaleReturnItem returnedItem :
                        saleReturn.getItems()) {

                    String key = createProductKey(
                            returnedItem.getProductId(),
                            returnedItem.getSku()
                    );

                    ProductAccumulator product =
                            products.get(key);

                    if (product == null) {
                        continue;
                    }

                    // הפחתת הכמות שהוחזרה
                    product.quantitySold -=
                            returnedItem.getQuantity();

                    // הפחתת ההכנסה בגין ההחזרה
                    product.revenue =
                            product.revenue.subtract(
                                    safe(returnedItem.getTotalPrice())
                            );

                    // עלות הקנייה ההיסטורית של הפריט שהוחזר
                    BigDecimal returnCost =
                            returnedItem.getPurchasePriceAtReturn();

                    if (returnCost == null) {

                        saleHasMissingCost = true;
                        allCostsAvailable = false;
                        product.missingCost = true;

                    } else {

                        BigDecimal returnedCost =
                                returnCost.multiply(
                                        BigDecimal.valueOf(
                                                returnedItem.getQuantity()
                                        )
                                );

                        returnedGoodsCost =
                                returnedGoodsCost.add(returnedCost);

                        product.cost =
                                product.cost.subtract(returnedCost);
                    }
                }
            }

            if (saleHasMissingCost) {
                salesWithMissingCost++;
            }
        }

        // הכנסה נטו
        BigDecimal netRevenue =
                totalRevenue.subtract(totalRefunds);

        // עלות הסחורה שנשארה לאחר החזרות
        BigDecimal netCost =
                costOfGoodsSold.subtract(returnedGoodsCost);

        // רווח גולמי מוצג רק כאשר כל נתוני העלות קיימים
        BigDecimal grossProfit = null;
        BigDecimal profitMarginPercentage = null;

        if (allCostsAvailable) {

            grossProfit = netRevenue.subtract(netCost);

            if (netRevenue.compareTo(BigDecimal.ZERO) != 0) {

                profitMarginPercentage =
                        grossProfit.multiply(BigDecimal.valueOf(100))
                                .divide(
                                        netRevenue,
                                        2,
                                        RoundingMode.HALF_UP
                                );
            } else {
                profitMarginPercentage = BigDecimal.ZERO;
            }
        }

        // בניית פירוט רווחיות לפי מוצר ווריאנט
        List<ProfitabilityReportDTO.ProductProfit> productProfits =
                new ArrayList<>();

        for (ProductAccumulator product : products.values()) {

            BigDecimal productProfit = null;

            if (!product.missingCost) {
                productProfit =
                        product.revenue.subtract(product.cost);
            }

            productProfits.add(
                    new ProfitabilityReportDTO.ProductProfit(
                            product.productId,
                            product.productName,
                            product.sku,
                            product.quantitySold,
                            money(product.revenue),
                            money(product.cost),
                            productProfit == null
                                    ? null
                                    : money(productProfit)
                    )
            );
        }

        return new ProfitabilityReportDTO(
                startDate.toString(),
                endDate.toString(),
                totalSales,
                money(totalRevenue),
                money(totalRefunds),
                money(netRevenue),
                money(costOfGoodsSold),
                money(returnedGoodsCost),
                grossProfit == null ? null : money(grossProfit),
                profitMarginPercentage,
                salesWithMissingCost,
                productProfits
        );
    }

    private String createProductKey(
            String productId,
            String sku
    ) {
        return productId + "::" + sku;
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    // מחלקה פנימית לצבירת נתוני המוצרים
    private static class ProductAccumulator {

        private final String productId;
        private final String productName;
        private final String sku;

        private int quantitySold;

        private BigDecimal revenue = BigDecimal.ZERO;
        private BigDecimal cost = BigDecimal.ZERO;

        private boolean missingCost = false;

        private ProductAccumulator(
                String productId,
                String productName,
                String sku
        ) {
            this.productId = productId;
            this.productName = productName;
            this.sku = sku;
        }
    }
}
