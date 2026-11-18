package SmartPOS_Server.repositories;

import SmartPOS_Server.models.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SaleRepository extends MongoRepository<Sale, String> {

    List<Sale> findByCustomerId(String customerId);

}