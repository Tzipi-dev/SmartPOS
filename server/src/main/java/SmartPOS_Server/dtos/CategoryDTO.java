package SmartPOS_Server.dtos;

import jakarta.validation.constraints.NotBlank;

public record CategoryDTO(
        @NotBlank(message = "יש להזין שם קטגוריה")
        String name,
        String description,
        String imageUrl
) {}