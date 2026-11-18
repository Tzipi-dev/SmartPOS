
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.PurchaseOrderRequestDTO;
import SmartPOS_Server.interfaces.IPurchaseOrderService;
import SmartPOS_Server.models.PurchaseOrder;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import SmartPOS_Server.dtos.PurchaseOrderReceiptRequestDTO;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final IPurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(
            IPurchaseOrderService purchaseOrderService
    ) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {
        return ResponseEntity.ok(
                purchaseOrderService.getAllPurchaseOrders()
        );
    }
    @PostMapping("/{id}/receive")
    public ResponseEntity<PurchaseOrder> receivePurchaseOrder(
            @PathVariable String id,
            @Valid @RequestBody PurchaseOrderReceiptRequestDTO request
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.receivePurchaseOrder(id, request)
        );
    }
    @PutMapping("/{id}/submit")
    public ResponseEntity<PurchaseOrder> submitPurchaseOrder(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.submitPurchaseOrder(id)
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrderById(id)
        );
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>> getBySupplier(
            @PathVariable String supplierId
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrdersBySupplier(supplierId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PurchaseOrder>> getByStatus(
            @PathVariable String status
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrdersByStatus(status)
        );
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseOrderService.createPurchaseOrder(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseOrder> updatePurchaseOrder(
            @PathVariable String id,
            @Valid @RequestBody PurchaseOrderRequestDTO request
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.updatePurchaseOrder(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PurchaseOrder> deletePurchaseOrder(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(
                purchaseOrderService.deletePurchaseOrder(id)
        );
    }
}