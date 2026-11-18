
package SmartPOS_Server.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PurchaseOrderReceiptItemDTO(

        @NotBlank
        String productId,

        @NotBlank
        String sku,

        @NotNull
        @Min(1)
        Integer quantity

) {
}