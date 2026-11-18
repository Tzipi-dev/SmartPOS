
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.PurchaseOrderRequestDTO;
import SmartPOS_Server.interfaces.IPurchaseOrderService;
import SmartPOS_Server.mappers.PurchaseOrderMapper;
import SmartPOS_Server.models.Product;
import SmartPOS_Server.models.ProductVariant;
import SmartPOS_Server.models.PurchaseOrder;
import SmartPOS_Server.models.PurchaseOrderItem;
import SmartPOS_Server.repositories.ProductRepository;
import SmartPOS_Server.repositories.PurchaseOrderRepository;
import SmartPOS_Server.repositories.SupplierRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import SmartPOS_Server.dtos.PurchaseOrderReceiptItemDTO;
import SmartPOS_Server.dtos.PurchaseOrderReceiptRequestDTO;
import SmartPOS_Server.models.InventoryMovement;
import SmartPOS_Server.repositories.InventoryMovementRepository;

import java.util.LinkedHashMap;
import java.util.Map;
@Service
public class PurchaseOrderService implements IPurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final InventoryMovementRepository inventoryMovementRepository;
    public PurchaseOrderService(
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository,
            PurchaseOrderMapper purchaseOrderMapper,
            InventoryMovementRepository inventoryMovementRepository
    ) {
        this.inventoryMovementRepository = inventoryMovementRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public PurchaseOrder getPurchaseOrderById(String id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("הזמנת הרכש לא נמצאה"));
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrdersBySupplier(String supplierId) {

        if (!supplierRepository.existsById(supplierId)) {
            throw new RuntimeException("הספק לא נמצא");
        }

        return purchaseOrderRepository.findBySupplierId(supplierId);
    }

    @Override
    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        return purchaseOrderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrderRequestDTO request) {

        validateSupplier(request.supplierId());

        PurchaseOrder order = purchaseOrderMapper.toEntity(request);

        enrichAndValidateItems(order.getItems());

        calculateTotals(order);

        return purchaseOrderRepository.save(order);
    }

    @Override
    @Transactional
    public PurchaseOrder updatePurchaseOrder(
            String id,
            PurchaseOrderRequestDTO request
    ) {

        PurchaseOrder order = getPurchaseOrderById(id);

        if (!"DRAFT".equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "ניתן לעדכן רק הזמנת רכש בטיוטה"
            );
        }

        validateSupplier(request.supplierId());

        purchaseOrderMapper.updateEntity(order, request);

        enrichAndValidateItems(order.getItems());

        calculateTotals(order);

        order.setUpdatedAt(LocalDateTime.now());

        return purchaseOrderRepository.save(order);
    }

    @Override
    public PurchaseOrder deletePurchaseOrder(String id) {

        PurchaseOrder order = getPurchaseOrderById(id);

        if (!"DRAFT".equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "ניתן למחוק רק הזמנת רכש בטיוטה"
            );
        }

        purchaseOrderRepository.delete(order);

        return order;
    }
    @Override
    @Transactional
    public PurchaseOrder receivePurchaseOrder(
            String id,
            PurchaseOrderReceiptRequestDTO request
    ) {

        PurchaseOrder order = getPurchaseOrderById(id);

        if (!"ORDERED".equals(order.getStatus())
                && !"PARTIALLY_RECEIVED".equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "ניתן לקבל סחורה רק מהזמנה שנשלחה לספק"
            );
        }

        // איחוד שורות כפולות של אותו מוצר ואותו מק״ט
        Map<String, Integer> quantitiesToReceive = new LinkedHashMap<>();

        for (PurchaseOrderReceiptItemDTO item : request.items()) {
            String key = item.productId() + "::" + item.sku();

            quantitiesToReceive.merge(
                    key,
                    item.quantity(),
                    Integer::sum
            );
        }

        // בדיקת הכמויות ועדכון ההזמנה והמלאי
        for (Map.Entry<String, Integer> entry : quantitiesToReceive.entrySet()) {

            String key = entry.getKey();
            int quantityToReceive = entry.getValue();

            String[] parts = key.split("::", 2);
            String productId = parts[0];
            String sku = parts[1];

            PurchaseOrderItem orderItem = order.getItems()
                    .stream()
                    .filter(item ->
                            item.getProductId().equals(productId)
                                    && item.getSku().equals(sku))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "הפריט לא נמצא בהזמנת הרכש: " + sku
                            ));

            int remainingQuantity =
                    orderItem.getQuantity() - orderItem.getReceivedQuantity();

            if (quantityToReceive > remainingQuantity) {
                throw new IllegalArgumentException(
                        "כמות הקבלה גדולה מהכמות שנותרה להזמנה עבור מק״ט: " + sku
                );
            }

            Product product = productRepository.findById(productId)
                    .orElseThrow(() ->
                            new RuntimeException("המוצר לא נמצא: " + productId));

            ProductVariant variant = product.getVariants()
                    .stream()
                    .filter(v -> v.getSku().equals(sku))
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("וריאנט המוצר לא נמצא: " + sku));

            // עדכון מלאי המוצר
            variant.setQuantity(variant.getQuantity() + quantityToReceive);

            productRepository.save(product);

            // עדכון הכמות שהתקבלה בהזמנה
            orderItem.setReceivedQuantity(
                    orderItem.getReceivedQuantity() + quantityToReceive
            );

            // יצירת תנועת מלאי
            InventoryMovement movement = new InventoryMovement();

            movement.setProductId(productId);
            movement.setSku(sku);
            movement.setMovementType("IN");
            movement.setQuantity(quantityToReceive);

            String reason = "קבלת סחורה מהזמנת רכש " + id;

            if (request.notes() != null && !request.notes().isBlank()) {
                reason += " - " + request.notes();
            }

            movement.setReason(reason);

            inventoryMovementRepository.save(movement);
        }

        // בדיקה אם כל הפריטים התקבלו
        boolean allReceived = order.getItems()
                .stream()
                .allMatch(item ->
                        item.getReceivedQuantity() >= item.getQuantity()
                );

        order.setStatus(
                allReceived ? "RECEIVED" : "PARTIALLY_RECEIVED"
        );

        order.setUpdatedAt(LocalDateTime.now());

        return purchaseOrderRepository.save(order);
    }
    @Override
    public PurchaseOrder submitPurchaseOrder(String id) {

        PurchaseOrder order = getPurchaseOrderById(id);

        if (!"DRAFT".equals(order.getStatus())) {
            throw new IllegalArgumentException(
                    "ניתן לשלוח לספק רק הזמנה שנמצאת בטיוטה"
            );
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "לא ניתן לשלוח הזמנה ללא פריטים"
            );
        }

        order.setStatus("ORDERED");
        order.setUpdatedAt(LocalDateTime.now());

        return purchaseOrderRepository.save(order);
    }
    private void validateSupplier(String supplierId) {

        if (!supplierRepository.existsById(supplierId)) {
            throw new RuntimeException("הספק לא נמצא");
        }
    }

    private void enrichAndValidateItems(List<PurchaseOrderItem> items) {

        for (PurchaseOrderItem item : items) {

            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new RuntimeException("המוצר לא נמצא: " + item.getProductId()));

            ProductVariant variant = product.getVariants()
                    .stream()
                    .filter(v -> v.getSku().equals(item.getSku()))
                    .findFirst()
                    .orElseThrow(() ->
                            new RuntimeException("וריאנט המוצר לא נמצא: " + item.getSku()));

            item.setProductName(product.getName());

            item.setTotalPrice(
                    item.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
    }

    private void calculateTotals(PurchaseOrder order) {

        BigDecimal subtotal = order.getItems()
                .stream()
                .map(PurchaseOrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = order.getDiscount() == null
                ? BigDecimal.ZERO
                : order.getDiscount();

        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(
                    "ההנחה לא יכולה להיות גדולה מסכום ההזמנה"
            );
        }

        order.setSubtotal(subtotal);
        order.setDiscount(discount);
        order.setTotal(subtotal.subtract(discount));
    }
}