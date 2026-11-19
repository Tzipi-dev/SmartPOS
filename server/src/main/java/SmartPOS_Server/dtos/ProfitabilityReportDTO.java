
package SmartPOS_Server.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ProfitabilityReportDTO(
        String startDate,
        String endDate,
        long totalSales,
        BigDecimal totalRevenue,
        BigDecimal totalRefunds,
        BigDecimal netRevenue,
        BigDecimal costOfGoodsSold,
        BigDecimal returnedGoodsCost,
        BigDecimal grossProfit,
        BigDecimal profitMarginPercentage,
        int salesWithMissingCost,
        List<ProductProfit> productProfits
) {
    public record ProductProfit(
            String productId,
            String productName,
            String sku,
            int quantitySold,
            BigDecimal revenue,
            BigDecimal cost,
            BigDecimal grossProfit
    ) {}
}
