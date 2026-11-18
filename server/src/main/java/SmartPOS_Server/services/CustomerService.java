
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.CustomerDTO;
import SmartPOS_Server.exceptions.ResourceNotFoundException;
import SmartPOS_Server.interfaces.ICustomerService;
import SmartPOS_Server.mappers.CustomerMapper;
import SmartPOS_Server.models.Customer;
import SmartPOS_Server.repositories.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public CustomerService(
            CustomerRepository customerRepository,
            CustomerMapper customerMapper) {

        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer getCustomerById(String id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("הלקוח לא נמצא"));
    }

    @Override
    public Customer addCustomer(CustomerDTO customerDTO) {
        checkDuplicatePhone(customerDTO.phone(), null);

        Customer customer = customerMapper.toEntity(customerDTO);

        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(String id, CustomerDTO customerDTO) {
        Customer existingCustomer = getCustomerById(id);

        checkDuplicatePhone(customerDTO.phone(), id);

        customerMapper.updateEntity(existingCustomer, customerDTO);

        return customerRepository.save(existingCustomer);
    }

    @Override
    public void deleteCustomer(String id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }

    @Override
    public List<Customer> searchCustomers(String search) {
        if (search == null || search.isBlank()) {
            return getAllCustomers();
        }

        String value = search.trim();

        return customerRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        value, value
                );
    }

    private void checkDuplicatePhone(String phone, String currentId) {
        customerRepository.findByPhone(phone)
                .filter(customer -> !customer.getId().equals(currentId))
                .ifPresent(customer -> {
                    throw new IllegalArgumentException(
                            "כבר קיים לקוח עם מספר הטלפון הזה"
                    );
                });
    }
}
