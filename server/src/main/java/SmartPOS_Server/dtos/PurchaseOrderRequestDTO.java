
package SmartPOS_Server.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseOrderRequestDTO(

        @NotBlank
        String supplierId,

        @NotEmpty
        List<@Valid PurchaseOrderItemDTO> items,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal discount

) {
}