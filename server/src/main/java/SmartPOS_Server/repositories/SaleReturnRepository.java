
package SmartPOS_Server.repositories;

import SmartPOS_Server.models.SaleReturn;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SaleReturnRepository
        extends MongoRepository<SaleReturn, String> {

    List<SaleReturn> findBySaleId(String saleId);
}
