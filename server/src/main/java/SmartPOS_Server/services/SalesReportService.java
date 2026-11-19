
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.SalesReportDTO;
import SmartPOS_Server.interfaces.ISalesReportService;
import SmartPOS_Server.models.Sale;
import SmartPOS_Server.models.SaleReturn;
import SmartPOS_Server.repositories.SaleRepository;
import SmartPOS_Server.repositories.SaleReturnRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SalesReportService implements ISalesReportService {

    private final SaleRepository saleRepository;
    private final SaleReturnRepository saleReturnRepository;

    public SalesReportService(
            SaleRepository saleRepository,
            SaleReturnRepository saleReturnRepository) {

        this.saleRepository = saleRepository;
        this.saleReturnRepository = saleReturnRepository;
    }

    @Override
    public SalesReportDTO getSalesReport(
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException(
                    "יש להזין תאריך התחלה ותאריך סיום");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "תאריך ההתחלה לא יכול להיות אחרי תאריך הסיום");
        }

        LocalDateTime start = startDate.atStartOfDay();

        LocalDateTime end = endDate.plusDays(1)
                .atStartOfDay()
                .minusNanos(1);

        // שליפת המכירות שהושלמו בטווח התאריכים
        List<Sale> sales =
                saleRepository.findCompletedOrLegacySalesBetween(
                        start,
                        end
                );

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (Sale sale : sales) {

            if (sale.getTotal() != null) {
                totalRevenue = totalRevenue.add(sale.getTotal());
            }

            if (sale.getDiscount() != null) {
                totalDiscount = totalDiscount.add(sale.getDiscount());
            }
        }

        // איסוף מזהי המכירות
        List<String> saleIds = sales.stream()
                .map(Sale::getId)
                .toList();

        // שליפת כל ההחזרות עבור המכירות בדוח
        List<SaleReturn> returns = saleIds.isEmpty()
                ? List.of()
                : saleReturnRepository.findBySaleIdIn(saleIds);

        BigDecimal totalRefunds = BigDecimal.ZERO;

        for (SaleReturn saleReturn : returns) {

            if (saleReturn.getRefundAmount() != null) {
                totalRefunds = totalRefunds.add(
                        saleReturn.getRefundAmount()
                );
            }
        }

        // הכנסה נטו לאחר החזרות
        BigDecimal netRevenue = totalRevenue.subtract(totalRefunds);

        long totalSales = sales.size();

        BigDecimal averageSale = totalSales == 0
                ? BigDecimal.ZERO
                : totalRevenue.divide(
                BigDecimal.valueOf(totalSales),
                2,
                RoundingMode.HALF_UP
        );

        BigDecimal averageNetSale = totalSales == 0
                ? BigDecimal.ZERO
                : netRevenue.divide(
                BigDecimal.valueOf(totalSales),
                2,
                RoundingMode.HALF_UP
        );

        return new SalesReportDTO(
                startDate.toString(),
                endDate.toString(),
                totalSales,
                totalRevenue,
                totalRefunds,
                netRevenue,
                totalDiscount,
                averageSale,
                averageNetSale
        );
    }
}
