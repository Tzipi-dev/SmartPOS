
package SmartPOS_Server.dtos;

import java.math.BigDecimal;

public record SalesReportDTO(
        String startDate,
        String endDate,
        long totalSales,

        // הכנסות ברוטו לפני החזרות
        BigDecimal totalRevenue,

        // סכום ההחזרות ללקוחות
        BigDecimal totalRefunds,

        // הכנסות לאחר ניכוי החזרות
        BigDecimal netRevenue,

        BigDecimal totalDiscount,

        // ממוצע מכירה ברוטו
        BigDecimal averageSale,

        // ממוצע הכנסה נטו לעסקה
        BigDecimal averageNetSale
) {
}
