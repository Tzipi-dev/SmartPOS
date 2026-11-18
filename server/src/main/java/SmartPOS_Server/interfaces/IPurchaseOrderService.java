
package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.PurchaseOrderRequestDTO;
import SmartPOS_Server.models.PurchaseOrder;
import SmartPOS_Server.dtos.PurchaseOrderReceiptRequestDTO;
import java.util.List;

public interface IPurchaseOrderService {

    List<PurchaseOrder> getAllPurchaseOrders();

    PurchaseOrder getPurchaseOrderById(String id);

    List<PurchaseOrder> getPurchaseOrdersBySupplier(String supplierId);

    List<PurchaseOrder> getPurchaseOrdersByStatus(String status);

    PurchaseOrder createPurchaseOrder(PurchaseOrderRequestDTO request);
    PurchaseOrder submitPurchaseOrder(String id);

    PurchaseOrder updatePurchaseOrder(
            String id,
            PurchaseOrderRequestDTO request
    );
    PurchaseOrder receivePurchaseOrder(
            String id,
            PurchaseOrderReceiptRequestDTO request
    );
    PurchaseOrder deletePurchaseOrder(String id);
}