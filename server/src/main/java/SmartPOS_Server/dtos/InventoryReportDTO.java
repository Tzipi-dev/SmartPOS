
package SmartPOS_Server.dtos;

import java.util.List;

public record InventoryReportDTO(
        String startDate,
        String endDate,
        int totalVariants,
        int totalUnits,
        int lowStockVariants,
        List<StockItem> stockItems,
        List<MovementSummary> movementSummaries
) {
    public record StockItem(
            String productId,
            String productName,
            String sku,
            String barcode,
            int quantity,
            int minimumQuantity,
            boolean lowStock
    ) {}

    public record MovementSummary(
            String movementType,
            long movementCount,
            Integer totalQuantity
    ) {}
}
