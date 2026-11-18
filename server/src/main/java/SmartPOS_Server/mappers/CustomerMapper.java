
package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.CustomerDTO;
import SmartPOS_Server.models.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();

        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setPhone(dto.phone());
        customer.setEmail(dto.email());
        customer.setAddress(dto.address());
        customer.setNotes(dto.notes());

        return customer;
    }

    public void updateEntity(Customer customer, CustomerDTO dto) {
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setPhone(dto.phone());
        customer.setEmail(dto.email());
        customer.setAddress(dto.address());
        customer.setNotes(dto.notes());
    }
}
