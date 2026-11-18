
package SmartPOS_Server.services;

import SmartPOS_Server.dtos.InventoryMovementDTO;
import SmartPOS_Server.interfaces.IInventoryService;
import SmartPOS_Server.mappers.InventoryMapper;
import SmartPOS_Server.models.InventoryMovement;
import SmartPOS_Server.models.Product;
import SmartPOS_Server.models.ProductVariant;
import SmartPOS_Server.repositories.InventoryMovementRepository;
import SmartPOS_Server.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService implements IInventoryService {

    private final InventoryMovementRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryService(
            InventoryMovementRepository inventoryRepository,
            ProductRepository productRepository,
            InventoryMapper inventoryMapper) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public List<InventoryMovement> getAllMovements() {
        return inventoryRepository.findAll();
    }

    @Override
    public List<InventoryMovement> getMovementsByProduct(String productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public List<InventoryMovement> getMovementsBySku(String sku) {
        return inventoryRepository.findBySku(sku);
    }

    @Override
    public InventoryMovement addMovement(InventoryMovementDTO dto) {

        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() ->
                        new RuntimeException("המוצר לא נמצא"));

        ProductVariant variant = product.getVariants().stream()
                .filter(v -> v.getSku().equals(dto.sku()))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("וריאנט המוצר לא נמצא"));

        int currentQuantity = variant.getQuantity();
        int newQuantity;

        switch (dto.movementType()) {

            case "IN" ->
                    newQuantity = currentQuantity + dto.quantity();

            case "OUT" -> {
                if (currentQuantity < dto.quantity()) {
                    throw new IllegalArgumentException(
                            "אין מספיק מלאי לביצוע הפעולה");
                }
                newQuantity = currentQuantity - dto.quantity();
            }

            case "ADJUSTMENT" ->
                    newQuantity = dto.quantity();

            default ->
                    throw new IllegalArgumentException(
                            "סוג תנועת מלאי לא תקין");
        }

        variant.setQuantity(newQuantity);

        productRepository.save(product);

        InventoryMovement movement = inventoryMapper.toEntity(dto);

        return inventoryRepository.save(movement);
    }
}
