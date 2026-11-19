
package SmartPOS_Server.repositories;

import SmartPOS_Server.models.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends MongoRepository<Sale, String> {

    List<Sale> findByCustomerId(String customerId);

    // מכירות לפי סטטוס
    List<Sale> findBySaleStatus(String saleStatus);

    // מכירות שהושלמו או מכירות ישנות ללא סטטוס, בטווח תאריכים
    @Query("{ 'createdAt': { '$gte': ?0, '$lte': ?1 }, '$or': [ { 'saleStatus': 'COMPLETED' }, { 'saleStatus': null } ] }")
    List<Sale> findCompletedOrLegacySalesBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // מכירות לפי אמצעי תשלום
    List<Sale> findBySaleStatusAndPaymentMethod(
            String saleStatus,
            String paymentMethod
    );

    // מכירות של לקוח בטווח תאריכים
    List<Sale> findByCustomerIdAndSaleStatusAndCreatedAtBetween(
            String customerId,
            String saleStatus,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
}
