
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.SaleRequestDTO;
import SmartPOS_Server.interfaces.ISaleService;
import SmartPOS_Server.models.Sale;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SaleController {

    private final ISaleService saleService;

    public SaleController(ISaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> getSaleById(@PathVariable String id) {
        return ResponseEntity.ok(saleService.getSaleById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Sale>> getSalesByCustomer(
            @PathVariable String customerId) {
        return ResponseEntity.ok(
                saleService.getSalesByCustomer(customerId)
        );
    }

    @PostMapping
    public ResponseEntity<Sale> createSale(
            @Valid @RequestBody SaleRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saleService.createSale(dto));
    }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Sale> cancelSale(
            @PathVariable String id,
            @RequestParam(required = false) String reason) {

        return ResponseEntity.ok(
                saleService.cancelSale(id, reason)
        );
    }
}
