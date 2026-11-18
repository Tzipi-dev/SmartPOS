
package SmartPOS_Server.repositories;

import SmartPOS_Server.models.PurchaseOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PurchaseOrderRepository
        extends MongoRepository<PurchaseOrder, String> {

    List<PurchaseOrder> findBySupplierId(String supplierId);

    List<PurchaseOrder> findByStatus(String status);
}