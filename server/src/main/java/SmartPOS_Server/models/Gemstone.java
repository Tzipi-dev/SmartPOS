package SmartPOS_Server.models;

public class Gemstone {

    private String type;
    private Double carat;
    private String description;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getCarat() { return carat; }
    public void setCarat(Double carat) { this.carat = carat; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }
}