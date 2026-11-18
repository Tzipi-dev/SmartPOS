
package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.CustomerDTO;
import SmartPOS_Server.models.Customer;

import java.util.List;

public interface ICustomerService {

    List<Customer> getAllCustomers();

    Customer getCustomerById(String id);

    Customer addCustomer(CustomerDTO customerDTO);

    Customer updateCustomer(String id, CustomerDTO customerDTO);

    void deleteCustomer(String id);

    List<Customer> searchCustomers(String search);
}
