
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.ProductRequestDTO;
import SmartPOS_Server.exceptions.ResourceNotFoundException;
import SmartPOS_Server.interfaces.IProductService;
import SmartPOS_Server.mappers.ProductMapper;
import SmartPOS_Server.models.Product;
import SmartPOS_Server.repositories.CategoryRepository;
import SmartPOS_Server.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ProductMapper productMapper) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(String id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("המוצר לא נמצא"));
    }

    @Override
    public Product addProduct(ProductRequestDTO productDTO) {

        validateCategory(productDTO.categoryId());

        Product product = productMapper.toEntity(productDTO);

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, ProductRequestDTO productDTO) {

        Product existingProduct = getProductById(id);

        validateCategory(productDTO.categoryId());

        productMapper.updateEntity(existingProduct, productDTO);

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(String id) {

        Product product = getProductById(id);

        productRepository.delete(product);
    }

    private void validateCategory(String categoryId) {

        if (categoryId == null || categoryId.isBlank()) {
            throw new IllegalArgumentException("יש לבחור קטגוריה למוצר");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("הקטגוריה לא נמצאה");
        }
    }
    @Override
    public List<Product> getProductsByCategory(String categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("הקטגוריה לא נמצאה");
        }

        return productRepository.findByCategoryId(categoryId);
    }
}
