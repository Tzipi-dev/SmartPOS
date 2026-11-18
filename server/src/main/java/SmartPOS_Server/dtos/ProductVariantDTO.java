package SmartPOS_Server.dtos;

import java.math.BigDecimal;
import java.util.Map;

public record ProductVariantDTO(
        String sku,
        String barcode,
        Map<String, String> attributes,
        BigDecimal purchasePrice,
        BigDecimal sellingPrice,
        int quantity,
        int minimumQuantity
) {}