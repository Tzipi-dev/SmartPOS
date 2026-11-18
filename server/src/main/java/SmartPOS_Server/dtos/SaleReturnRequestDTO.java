
package SmartPOS_Server.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SaleReturnRequestDTO(

        @NotBlank(message = "יש להזין מזהה מכירה")
        String saleId,

        @NotEmpty(message = "יש להזין לפחות מוצר אחד להחזרה")
        List<@Valid SaleReturnItemDTO> items,

        String reason

) {}
