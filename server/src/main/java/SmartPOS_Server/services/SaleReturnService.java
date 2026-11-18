
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.SaleReturnRequestDTO;
import SmartPOS_Server.dtos.SaleReturnItemDTO;
import SmartPOS_Server.interfaces.ISaleReturnService;
import SmartPOS_Server.mappers.SaleReturnMapper;
import SmartPOS_Server.models.*;
import SmartPOS_Server.repositories.*;

import SmartPOS_Server.exceptions.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SaleReturnService implements ISaleReturnService {

    private final SaleReturnRepository returnRepository;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final InventoryMovementRepository inventoryRepository;
    private final SaleReturnMapper returnMapper;

    public SaleReturnService(
            SaleReturnRepository returnRepository,
            SaleRepository saleRepository,
            ProductRepository productRepository,
            InventoryMovementRepository inventoryRepository,
            SaleReturnMapper returnMapper) {

        this.returnRepository = returnRepository;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.returnMapper = returnMapper;
    }

    @Override
    public List<SaleReturn> getAllReturns() {
        return returnRepository.findAll();
    }

    @Override
    public SaleReturn getReturnById(String id) {
        return returnRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("ההחזרה לא נמצאה"));
    }

    @Override
    public List<SaleReturn> getReturnsBySale(String saleId) {
        return returnRepository.findBySaleId(saleId);
    }

    @Override
    @Transactional
    public SaleReturn createReturn(SaleReturnRequestDTO dto) {

        Sale sale = saleRepository.findById(dto.saleId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("המכירה לא נמצאה"));

        // מאחדים כמויות לפי מוצר ומק״ט
        Map<String, Integer> requestedQuantities = new HashMap<>();

        for (SaleReturnItemDTO itemDTO : dto.items()) {
            String key = itemDTO.productId() + "::" + itemDTO.sku();

            requestedQuantities.merge(
                    key,
                    itemDTO.quantity(),
                    Integer::sum
            );
        }

        // מאחדים את פריטי המכירה המקורית לפי מוצר ומק״ט
        Map<String, List<SaleItem>> soldItems = new HashMap<>();

        for (SaleItem item : sale.getItems()) {
            String key = item.getProductId() + "::" + item.getSku();

            soldItems.computeIfAbsent(
                    key,
                    k -> new ArrayList<>()
            ).add(item);
        }

        // מחשבים כמה כבר הוחזר בכל החזרה קודמת
        Map<String, Integer> alreadyReturned = new HashMap<>();
        List<SaleReturn> previousReturns =
                returnRepository.findBySaleId(dto.saleId());

        for (SaleReturn previousReturn : previousReturns) {

            if (previousReturn.getItems() == null) {
                continue;
            }

            for (SaleReturnItem item : previousReturn.getItems()) {

                String key = item.getProductId() + "::" + item.getSku();

                alreadyReturned.merge(
                        key,
                        item.getQuantity(),
                        Integer::sum
                );
            }
        }

        Map<String, Product> products = new HashMap<>();
        List<SaleReturnItem> returnItems = new ArrayList<>();

        BigDecimal refundAmount = BigDecimal.ZERO;

        BigDecimal discountRatio = BigDecimal.ZERO;

        if (sale.getSubtotal() != null
                && sale.getSubtotal().compareTo(BigDecimal.ZERO) > 0) {

            discountRatio = sale.getTotal().divide(
                    sale.getSubtotal(),
                    10,
                    RoundingMode.HALF_UP
            );
        }

        for (Map.Entry<String, Integer> entry :
                requestedQuantities.entrySet()) {

            String key = entry.getKey();
            int returnQuantity = entry.getValue();

            List<SaleItem> originalItems = soldItems.get(key);

            if (originalItems == null || originalItems.isEmpty()) {
                throw new IllegalArgumentException(
                        "המוצר אינו מופיע במכירה המקורית"
                );
            }

            int soldQuantity = 0;
            BigDecimal originalTotal = BigDecimal.ZERO;
            String productName = originalItems.get(0).getProductName();

            for (SaleItem originalItem : originalItems) {
                soldQuantity += originalItem.getQuantity();
                originalTotal = originalTotal.add(
                        originalItem.getTotalPrice()
                );
            }

            int previousQuantity = alreadyReturned.getOrDefault(key, 0);

            if (returnQuantity > soldQuantity - previousQuantity) {
                throw new IllegalArgumentException(
                        "כמות ההחזרה גדולה מהכמות שנותרה להחזרה עבור מק״ט: "
                                + originalItems.get(0).getSku()
                );
            }

            // חישוב ההחזר לפי המחיר ששולם בפועל, לאחר הנחה יחסית
            BigDecimal itemRefund = originalTotal
                    .multiply(BigDecimal.valueOf(returnQuantity))
                    .divide(
                            BigDecimal.valueOf(soldQuantity),
                            10,
                            RoundingMode.HALF_UP
                    )
                    .multiply(discountRatio)
                    .setScale(2, RoundingMode.HALF_UP);

            refundAmount = refundAmount.add(itemRefund);

            String productId = originalItems.get(0).getProductId();
            String sku = originalItems.get(0).getSku();

            Product product = products.computeIfAbsent(
                    productId,
                    id -> productRepository.findById(id)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "המוצר לא נמצא"
                                    ))
            );

            ProductVariant variant = product.getVariants().stream()
                    .filter(v -> v.getSku().equals(sku))
                    .findFirst()
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "וריאנט המוצר לא נמצא"
                            ));

            // החזרת הכמות למלאי
            variant.setQuantity(
                    variant.getQuantity() + returnQuantity
            );

            SaleReturnItem returnItem = new SaleReturnItem();
            returnItem.setProductId(productId);
            returnItem.setProductName(productName);
            returnItem.setSku(sku);
            returnItem.setQuantity(returnQuantity);
            returnItem.setTotalPrice(itemRefund);

            BigDecimal effectiveUnitPrice = itemRefund.divide(
                    BigDecimal.valueOf(returnQuantity),
                    2,
                    RoundingMode.HALF_UP
            );

            returnItem.setUnitPrice(effectiveUnitPrice);

            returnItems.add(returnItem);
        }

        // שומרים את פרטי ההחזרה
        SaleReturn saleReturn = new SaleReturn();
        saleReturn.setSaleId(dto.saleId());
        saleReturn.setItems(returnItems);
        saleReturn.setRefundAmount(refundAmount);
        saleReturn.setReason(dto.reason());

        SaleReturn savedReturn = returnRepository.save(saleReturn);

        // שומרים את המוצרים לאחר עדכון המלאי
        for (Product product : products.values()) {
            productRepository.save(product);
        }

        // יוצרים תנועת מלאי מסוג IN לכל מוצר שהוחזר
        for (SaleReturnItem item : returnItems) {
            InventoryMovement movement = new InventoryMovement();

            movement.setProductId(item.getProductId());
            movement.setSku(item.getSku());
            movement.setMovementType("IN");
            movement.setQuantity(item.getQuantity());
            movement.setReason(
                    "החזרה מספר " + savedReturn.getId()
                            + " למכירה " + dto.saleId()
            );

            inventoryRepository.save(movement);
        }

        return savedReturn;
    }
}