package SmartPOS_Server.repositories;

import SmartPOS_Server.models.InventoryMovement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InventoryMovementRepository
        extends MongoRepository<InventoryMovement, String> {

    List<InventoryMovement> findByProductId(String productId);

    List<InventoryMovement> findBySku(String sku);

    List<InventoryMovement> findByMovementType(String movementType);
}