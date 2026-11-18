
package SmartPOS_Server.repositories;

import SmartPOS_Server.models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository
        extends MongoRepository<Customer, String> {

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);

    List<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );
}
