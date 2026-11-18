
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.SaleRequestDTO;
import SmartPOS_Server.dtos.SaleItemDTO;
import SmartPOS_Server.interfaces.ISaleService;
import SmartPOS_Server.models.*;
import SmartPOS_Server.repositories.SaleRepository;
import SmartPOS_Server.repositories.ProductRepository;
import SmartPOS_Server.repositories.CustomerRepository;
import SmartPOS_Server.repositories.InventoryMovementRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class SaleService implements ISaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryMovementRepository inventoryRepository;

    public SaleService(
            SaleRepository saleRepository,
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            InventoryMovementRepository inventoryRepository) {

        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    @Override
    public Sale getSaleById(String id) {
        return saleRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("המכירה לא נמצאה"));
    }

    @Override
    public List<Sale> getSalesByCustomer(String customerId) {
        return saleRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional
    public Sale createSale(SaleRequestDTO dto) {

        // בדיקת לקוח
        if (dto.customerId() != null && !dto.customerId().isBlank()) {
            if (!customerRepository.existsById(dto.customerId())) {
                throw new RuntimeException("הלקוח לא נמצא");
            }
        }

        BigDecimal subtotal = BigDecimal.ZERO;

        List<SaleItem> saleItems = new ArrayList<>();

        Map<String, Product> products = new HashMap<>();
        Map<String, ProductVariant> variants = new HashMap<>();
        Map<String, Integer> requestedQuantities = new HashMap<>();

        // בניית פריטי המכירה
        for (SaleItemDTO itemDTO : dto.items()) {

            Product product = products.computeIfAbsent(
                    itemDTO.productId(),
                    id -> productRepository.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException("המוצר לא נמצא"))
            );

            String key = itemDTO.productId() + "::" + itemDTO.sku();

            ProductVariant variant = variants.get(key);

            if (variant == null) {
                variant = product.getVariants().stream()
                        .filter(v -> v.getSku().equals(itemDTO.sku()))
                        .findFirst()
                        .orElseThrow(() ->
                                new RuntimeException("וריאנט המוצר לא נמצא"));

                variants.put(key, variant);
            }

            requestedQuantities.merge(
                    key,
                    itemDTO.quantity(),
                    Integer::sum
            );

            BigDecimal unitPrice = variant.getSellingPrice();

            BigDecimal itemTotal = unitPrice.multiply(
                    BigDecimal.valueOf(itemDTO.quantity())
            );

            SaleItem saleItem = new SaleItem();
            saleItem.setProductId(product.getId());
            saleItem.setProductName(product.getName());
            saleItem.setSku(variant.getSku());
            saleItem.setQuantity(itemDTO.quantity());
            saleItem.setUnitPrice(unitPrice);
            saleItem.setTotalPrice(itemTotal);

            saleItems.add(saleItem);

            subtotal = subtotal.add(itemTotal);
        }

        // בדיקת מלאי לפי הכמות המצטברת
        for (Map.Entry<String, Integer> entry :
                requestedQuantities.entrySet()) {

            ProductVariant variant = variants.get(entry.getKey());

            if (variant.getQuantity() < entry.getValue()) {
                throw new IllegalArgumentException(
                        "אין מספיק מלאי עבור מק״ט: " + variant.getSku()
                );
            }
        }

        // חישוב מחיר
        BigDecimal discount = dto.discount();

        if (discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException(
                    "ההנחה אינה יכולה להיות גדולה מסכום המכירה"
            );
        }

        BigDecimal total = subtotal.subtract(discount);

        if (dto.amountPaid().compareTo(total) < 0) {
            throw new IllegalArgumentException(
                    "הסכום ששולם נמוך מסכום המכירה"
            );
        }

        BigDecimal change = dto.amountPaid().subtract(total);

        // יצירת מכירה
        Sale sale = new Sale();

        sale.setCustomerId(dto.customerId());
        sale.setItems(saleItems);
        sale.setSubtotal(subtotal);
        sale.setDiscount(discount);
        sale.setTotal(total);
        sale.setPaymentMethod(dto.paymentMethod());
        sale.setAmountPaid(dto.amountPaid());
        sale.setChange(change);
        sale.setPaymentStatus("PAID");

        // שמירת המכירה
        Sale savedSale = saleRepository.save(sale);

        // הפחתת מלאי
        for (Map.Entry<String, Integer> entry :
                requestedQuantities.entrySet()) {

            ProductVariant variant = variants.get(entry.getKey());

            variant.setQuantity(
                    variant.getQuantity() - entry.getValue()
            );
        }

        // שמירת כל מוצר פעם אחת
        for (Product product : products.values()) {
            productRepository.save(product);
        }

        // תיעוד תנועות המלאי
        for (Map.Entry<String, Integer> entry :
                requestedQuantities.entrySet()) {

            String[] keyParts = entry.getKey().split("::", 2);

            InventoryMovement movement = new InventoryMovement();

            movement.setProductId(keyParts[0]);
            movement.setSku(keyParts[1]);
            movement.setMovementType("OUT");
            movement.setQuantity(entry.getValue());
            movement.setReason("מכירה מספר " + savedSale.getId());

            inventoryRepository.save(movement);
        }

        return savedSale;
    }
}
