
package SmartPOS_Server.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaleReturnItemDTO(

        @NotBlank(message = "יש להזין מזהה מוצר")
        String productId,

        @NotBlank(message = "יש להזין מק״ט")
        String sku,

        @NotNull(message = "יש להזין כמות")
        @Min(value = 1, message = "כמות ההחזרה חייבת להיות לפחות 1")
        Integer quantity

) {}
