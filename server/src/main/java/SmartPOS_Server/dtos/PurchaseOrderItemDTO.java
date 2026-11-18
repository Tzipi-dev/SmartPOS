
package SmartPOS_Server.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PurchaseOrderItemDTO(

        @NotBlank
        String productId,

        @NotBlank
        String sku,

        @NotNull
        @Min(1)
        Integer quantity,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal unitPrice

) {
}