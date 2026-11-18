
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.CustomerDTO;
import SmartPOS_Server.interfaces.ICustomerService;
import SmartPOS_Server.models.Customer;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @GetMapping("/search")
    public List<Customer> searchCustomers(@RequestParam String query) {
        return customerService.searchCustomers(query);
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(
            @Valid @RequestBody CustomerDTO customerDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.addCustomer(customerDTO));
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(
            @PathVariable String id,
            @Valid @RequestBody CustomerDTO customerDTO) {

        return customerService.updateCustomer(id, customerDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
