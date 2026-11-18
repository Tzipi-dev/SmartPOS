package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.InventoryMovementDTO;
import SmartPOS_Server.models.InventoryMovement;

import java.util.List;

public interface IInventoryService {

    List<InventoryMovement> getAllMovements();

    List<InventoryMovement> getMovementsByProduct(String productId);

    List<InventoryMovement> getMovementsBySku(String sku);

    InventoryMovement addMovement(InventoryMovementDTO dto);
}