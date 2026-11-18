package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.InventoryMovementDTO;
import SmartPOS_Server.models.InventoryMovement;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryMovement toEntity(InventoryMovementDTO dto) {

        InventoryMovement movement = new InventoryMovement();

        movement.setProductId(dto.productId());
        movement.setSku(dto.sku());
        movement.setMovementType(dto.movementType());
        movement.setQuantity(dto.quantity());
        movement.setReason(dto.reason());

        return movement;
    }
}