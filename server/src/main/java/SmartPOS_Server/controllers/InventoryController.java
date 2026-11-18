
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.InventoryMovementDTO;
import SmartPOS_Server.interfaces.IInventoryService;
import SmartPOS_Server.models.InventoryMovement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final IInventoryService inventoryService;

    public InventoryController(IInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // הצגת כל תנועות המלאי
    @GetMapping("/movements")
    public ResponseEntity<List<InventoryMovement>> getAllMovements() {
        return ResponseEntity.ok(inventoryService.getAllMovements());
    }

    // הצגת תנועות לפי מוצר
    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<InventoryMovement>> getMovementsByProduct(
            @PathVariable String productId) {

        return ResponseEntity.ok(
                inventoryService.getMovementsByProduct(productId));
    }

    // הצגת תנועות לפי SKU
    @GetMapping("/movements/sku/{sku}")
    public ResponseEntity<List<InventoryMovement>> getMovementsBySku(
            @PathVariable String sku) {

        return ResponseEntity.ok(
                inventoryService.getMovementsBySku(sku));
    }

    // הוספת תנועת מלאי
    @PostMapping("/movement")
    public ResponseEntity<InventoryMovement> addMovement(
            @Valid @RequestBody InventoryMovementDTO dto) {

        InventoryMovement movement = inventoryService.addMovement(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(movement);
    }
}
