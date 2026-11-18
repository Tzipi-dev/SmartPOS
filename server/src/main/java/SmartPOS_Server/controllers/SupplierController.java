
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.SupplierDTO;
import SmartPOS_Server.interfaces.ISupplierService;
import SmartPOS_Server.models.Supplier;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "http://localhost:4200")
public class SupplierController {

    private final ISupplierService supplierService;

    public SupplierController(ISupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @GetMapping("/{id}")
    public Supplier getSupplierById(@PathVariable String id) {
        return supplierService.getSupplierById(id);
    }

    @GetMapping("/search")
    public List<Supplier> searchSuppliers(@RequestParam String query) {
        return supplierService.searchSuppliers(query);
    }

    @PostMapping
    public ResponseEntity<Supplier> addSupplier(
            @Valid @RequestBody SupplierDTO supplierDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.addSupplier(supplierDTO));
    }

    @PutMapping("/{id}")
    public Supplier updateSupplier(
            @PathVariable String id,
            @Valid @RequestBody SupplierDTO supplierDTO) {

        return supplierService.updateSupplier(id, supplierDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable String id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
