package SmartPOS_Server.models;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class ProductVariant {

    private String sku;
    private String barcode;

    // לדוגמה: מידה 52, צבע זהב צהוב, אורך 45 ס"מ
    private Map<String, String> attributes = new LinkedHashMap<>();

    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;

    private int quantity;
    private int minimumQuantity;

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes == null ? new LinkedHashMap<>() : attributes;
    }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getMinimumQuantity() { return minimumQuantity; }
    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }
}