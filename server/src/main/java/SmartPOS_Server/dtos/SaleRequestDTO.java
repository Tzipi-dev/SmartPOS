
package SmartPOS_Server.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record SaleRequestDTO(

        String customerId,

        @NotEmpty(message = "יש להוסיף לפחות מוצר אחד למכירה")
        List<@Valid SaleItemDTO> items,

        @NotNull(message = "יש להזין הנחה")
        @DecimalMin(value = "0.0", message = "ההנחה לא יכולה להיות שלילית")
        BigDecimal discount,

        @NotBlank(message = "יש לבחור אמצעי תשלום")
        String paymentMethod,

        @NotNull(message = "יש להזין סכום ששולם")
        @DecimalMin(value = "0.0", message = "הסכום ששולם לא יכול להיות שלילי")
        BigDecimal amountPaid

) {}
