
package SmartPOS_Server.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SupplierDTO(

        @NotBlank(message = "יש להזין שם ספק")
        String name,

        String contactPerson,
        String phone,

        @Email(message = "כתובת האימייל אינה תקינה")
        String email,

        String address,
        String taxId,
        String notes
) {}
