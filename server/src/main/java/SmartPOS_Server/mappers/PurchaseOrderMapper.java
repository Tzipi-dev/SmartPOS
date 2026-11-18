
package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.PurchaseOrderItemDTO;
import SmartPOS_Server.dtos.PurchaseOrderRequestDTO;
import SmartPOS_Server.models.PurchaseOrder;
import SmartPOS_Server.models.PurchaseOrderItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrder toEntity(PurchaseOrderRequestDTO dto) {

        PurchaseOrder order = new PurchaseOrder();

        order.setSupplierId(dto.supplierId());
        order.setDiscount(dto.discount());

        List<PurchaseOrderItem> items = dto.items()
                .stream()
                .map(this::toItemEntity)
                .toList();

        order.setItems(items);

        return order;
    }

    public void updateEntity(
            PurchaseOrder order,
            PurchaseOrderRequestDTO dto
    ) {

        order.setSupplierId(dto.supplierId());
        order.setDiscount(dto.discount());

        List<PurchaseOrderItem> items = dto.items()
                .stream()
                .map(this::toItemEntity)
                .toList();

        order.setItems(items);
        order.setUpdatedAt(java.time.LocalDateTime.now());
    }

    private PurchaseOrderItem toItemEntity(PurchaseOrderItemDTO dto) {

        PurchaseOrderItem item = new PurchaseOrderItem();

        item.setProductId(dto.productId());
        item.setSku(dto.sku());
        item.setQuantity(dto.quantity());
        item.setReceivedQuantity(0);
        item.setUnitPrice(dto.unitPrice());

        BigDecimal totalPrice = dto.unitPrice()
                .multiply(BigDecimal.valueOf(dto.quantity()));

        item.setTotalPrice(totalPrice);

        return item;
    }
}