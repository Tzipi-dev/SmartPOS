
package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.SaleReturnItemDTO;
import SmartPOS_Server.models.SaleReturnItem;
import org.springframework.stereotype.Component;

@Component
public class SaleReturnMapper {

    public SaleReturnItem toItemEntity(SaleReturnItemDTO dto) {

        SaleReturnItem item = new SaleReturnItem();

        item.setProductId(dto.productId());
        item.setSku(dto.sku());
        item.setQuantity(dto.quantity());

        return item;
    }
}
