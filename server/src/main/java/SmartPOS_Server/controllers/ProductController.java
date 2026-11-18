package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.ProductRequestDTO;
import SmartPOS_Server.interfaces.IProductService;
import SmartPOS_Server.models.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product addProduct(@RequestBody ProductRequestDTO product) {
        return productService.addProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(
            @PathVariable String id,
            @RequestBody ProductRequestDTO product) {

        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(
            @PathVariable String categoryId) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId)
        );
    }
}