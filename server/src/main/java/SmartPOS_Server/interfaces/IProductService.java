package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.ProductRequestDTO;
import SmartPOS_Server.models.Product;

import java.util.List;

public interface IProductService {

    List<Product> getAllProducts();

    Product getProductById(String id);

    Product addProduct(ProductRequestDTO product);

    Product updateProduct(String id, ProductRequestDTO product);

    void deleteProduct(String id);
}