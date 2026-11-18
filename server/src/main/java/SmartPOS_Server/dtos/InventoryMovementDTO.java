
package SmartPOS_Server.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryMovementDTO(

        @NotBlank(message = "יש להזין מזהה מוצר")
        String productId,

        @NotBlank(message = "יש להזין מק״ט")
        String sku,

        @NotBlank(message = "יש לבחור סוג תנועה")
        String movementType,

        @NotNull(message = "יש להזין כמות")
        @Min(value = 1, message = "הכמות חייבת להיות לפחות 1")
        Integer quantity,

        String reason
) {}
