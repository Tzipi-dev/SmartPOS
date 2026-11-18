package SmartPOS_Server.repositories;

import SmartPOS_Server.models.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository
        extends MongoRepository<Category, String> {

    Optional<Category> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}