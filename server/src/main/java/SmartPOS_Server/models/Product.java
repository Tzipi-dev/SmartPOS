
package SmartPOS_Server.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private String categoryId;

    private String metalType;
    private String purity;
    private Double weightGrams;

    private List<Gemstone> gemstones = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private List<ProductVariant> variants = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getMetalType() { return metalType; }
    public void setMetalType(String metalType) { this.metalType = metalType; }

    public String getPurity() { return purity; }
    public void setPurity(String purity) { this.purity = purity; }

    public Double getWeightGrams() { return weightGrams; }
    public void setWeightGrams(Double weightGrams) { this.weightGrams = weightGrams; }

    public List<Gemstone> getGemstones() { return gemstones; }
    public void setGemstones(List<Gemstone> gemstones) {
        this.gemstones = gemstones == null ? new ArrayList<>() : gemstones;
    }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls == null ? new ArrayList<>() : imageUrls;
    }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants == null ? new ArrayList<>() : variants;
    }
}
