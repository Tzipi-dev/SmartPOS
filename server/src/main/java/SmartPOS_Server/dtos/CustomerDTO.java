
package SmartPOS_Server.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerDTO(
        @NotBlank(message = "יש להזין שם פרטי")
        String firstName,

        @NotBlank(message = "יש להזין שם משפחה")
        String lastName,

        @NotBlank(message = "יש להזין מספר טלפון")
        String phone,

        @Email(message = "כתובת האימייל אינה תקינה")
        String email,

        String address,
        String notes
) {}
