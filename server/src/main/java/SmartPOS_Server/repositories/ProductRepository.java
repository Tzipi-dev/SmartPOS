package SmartPOS_Server.repositories;

import SmartPOS_Server.models.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository
        extends MongoRepository<Product, String> {
}