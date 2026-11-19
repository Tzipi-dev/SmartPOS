
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.InventoryReportDTO;
import SmartPOS_Server.interfaces.IInventoryReportService;
import SmartPOS_Server.models.InventoryMovement;
import SmartPOS_Server.models.Product;
import SmartPOS_Server.models.ProductVariant;
import SmartPOS_Server.repositories.InventoryMovementRepository;
import SmartPOS_Server.repositories.ProductRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryReportService implements IInventoryReportService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;

    public InventoryReportService(
            ProductRepository productRepository,
            InventoryMovementRepository movementRepository) {

        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
    }

    @Override
    public InventoryReportDTO getInventoryReport(
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

        // שליפת כל המוצרים והווריאנטים
        List<Product> products = productRepository.findAll();

        List<InventoryReportDTO.StockItem> stockItems =
                new ArrayList<>();

        int totalUnits = 0;
        int lowStockVariants = 0;

        for (Product product : products) {

            if (product.getVariants() == null) {
                continue;
            }

            for (ProductVariant variant : product.getVariants()) {

                int quantity = variant.getQuantity();
                int minimum = variant.getMinimumQuantity();

                boolean lowStock = quantity <= minimum;

                totalUnits += quantity;

                if (lowStock) {
                    lowStockVariants++;
                }

                stockItems.add(
                        new InventoryReportDTO.StockItem(
                                product.getId(),
                                product.getName(),
                                variant.getSku(),
                                variant.getBarcode(),
                                quantity,
                                minimum,
                                lowStock
                        )
                );
            }
        }

        // שליפת תנועות המלאי בטווח התאריכים
        LocalDateTime start = startDate.atStartOfDay();

        LocalDateTime end = endDate.plusDays(1)
                .atStartOfDay()
                .minusNanos(1);

        List<InventoryMovement> movements =
                movementRepository.findByCreatedAtBetween(
                        start,
                        end
                );

        long inCount = 0;
        long outCount = 0;
        long adjustmentCount = 0;

        int totalIn = 0;
        int totalOut = 0;

        for (InventoryMovement movement : movements) {

            switch (movement.getMovementType()) {

                case "IN" -> {
                    inCount++;
                    totalIn += movement.getQuantity();
                }

                case "OUT" -> {
                    outCount++;
                    totalOut += movement.getQuantity();
                }

                case "ADJUSTMENT" -> adjustmentCount++;

                default -> {
                    // סוג תנועה לא מוכר אינו נכלל בסיכום
                }
            }
        }

        List<InventoryReportDTO.MovementSummary> summaries =
                List.of(
                        new InventoryReportDTO.MovementSummary(
                                "IN", inCount, totalIn
                        ),
                        new InventoryReportDTO.MovementSummary(
                                "OUT", outCount, totalOut
                        ),
                        new InventoryReportDTO.MovementSummary(
                                "ADJUSTMENT", adjustmentCount, null
                        )
                );

        return new InventoryReportDTO(
                startDate.toString(),
                endDate.toString(),
                stockItems.size(),
                totalUnits,
                lowStockVariants,
                stockItems,
                summaries
        );
    }
}
