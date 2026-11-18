
package SmartPOS_Server.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PurchaseOrderReceiptRequestDTO(

        @NotEmpty
        List<@Valid PurchaseOrderReceiptItemDTO> items,

        String notes

) {
}