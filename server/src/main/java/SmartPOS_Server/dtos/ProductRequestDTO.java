package SmartPOS_Server.dtos;

import java.util.List;

public record ProductRequestDTO(
        String name,
        String description,
        String category,
        String metalType,
        String purity,
        Double weightGrams,
        List<GemstoneDTO> gemstones,
        List<String> imageUrls,
        List<ProductVariantDTO> variants
) {}