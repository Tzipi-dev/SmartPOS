
package SmartPOS_Server.repositories;

import SmartPOS_Server.models.Supplier;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SupplierRepository
        extends MongoRepository<Supplier, String> {

    List<Supplier> findByNameContainingIgnoreCaseOrContactPersonContainingIgnoreCase(
            String name,
            String contactPerson
    );
}
