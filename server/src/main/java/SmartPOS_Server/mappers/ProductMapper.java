package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.GemstoneDTO;
import SmartPOS_Server.dtos.ProductRequestDTO;
import SmartPOS_Server.dtos.ProductVariantDTO;
import SmartPOS_Server.models.Gemstone;
import SmartPOS_Server.models.Product;
import SmartPOS_Server.models.ProductVariant;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequestDTO dto) {

        Product product = new Product();

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategory(dto.category());
        product.setMetalType(dto.metalType());
        product.setPurity(dto.purity());
        product.setWeightGrams(dto.weightGrams());

        product.setImageUrls(
                dto.imageUrls() == null ? new ArrayList<>() : dto.imageUrls()
        );

        product.setGemstones(mapGemstones(dto.gemstones()));
        product.setVariants(mapVariants(dto.variants()));

        return product;
    }

    public void updateEntity(Product product, ProductRequestDTO dto) {

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setCategory(dto.category());
        product.setMetalType(dto.metalType());
        product.setPurity(dto.purity());
        product.setWeightGrams(dto.weightGrams());

        product.setImageUrls(
                dto.imageUrls() == null ? new ArrayList<>() : dto.imageUrls()
        );

        product.setGemstones(mapGemstones(dto.gemstones()));
        product.setVariants(mapVariants(dto.variants()));
    }

    private List<Gemstone> mapGemstones(List<GemstoneDTO> dtos) {

        if (dtos == null) {
            return new ArrayList<>();
        }

        return dtos.stream().map(dto -> {
            Gemstone gemstone = new Gemstone();
            gemstone.setType(dto.type());
            gemstone.setCarat(dto.carat());
            gemstone.setDescription(dto.description());
            return gemstone;
        }).toList();
    }

    private List<ProductVariant> mapVariants(List<ProductVariantDTO> dtos) {

        if (dtos == null) {
            return new ArrayList<>();
        }

        return dtos.stream().map(dto -> {
            ProductVariant variant = new ProductVariant();

            variant.setSku(dto.sku());
            variant.setBarcode(dto.barcode());
            variant.setAttributes(dto.attributes());
            variant.setPurchasePrice(dto.purchasePrice());
            variant.setSellingPrice(dto.sellingPrice());
            variant.setQuantity(dto.quantity());
            variant.setMinimumQuantity(dto.minimumQuantity());

            return variant;
        }).toList();
    }
}