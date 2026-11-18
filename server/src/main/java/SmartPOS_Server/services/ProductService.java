package SmartPOS_Server.services;

import SmartPOS_Server.dtos.ProductRequestDTO;
import SmartPOS_Server.exceptions.ResourceNotFoundException;
import SmartPOS_Server.interfaces.IProductService;
import SmartPOS_Server.mappers.ProductMapper;
import SmartPOS_Server.models.Product;
import SmartPOS_Server.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(
            ProductRepository productRepository,
            ProductMapper productMapper) {

        this.productRepository = productRepository;
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

        Product product = productMapper.toEntity(productDTO);

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, ProductRequestDTO productDTO) {

        Product existingProduct = getProductById(id);

        productMapper.updateEntity(existingProduct, productDTO);

        return productRepository.save(existingProduct);
    }

    @Override
    public void deleteProduct(String id) {

        Product product = getProductById(id);

        productRepository.delete(product);
    }
}