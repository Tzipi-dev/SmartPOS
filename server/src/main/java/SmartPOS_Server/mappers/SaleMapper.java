package SmartPOS_Server.mappers;

import SmartPOS_Server.dtos.SaleRequestDTO;
import SmartPOS_Server.models.Sale;
import SmartPOS_Server.models.SaleItem;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

    public Sale toEntity(SaleRequestDTO dto) {

        Sale sale = new Sale();

        sale.setCustomerId(dto.customerId());
        sale.setDiscount(dto.discount());
        sale.setPaymentMethod(dto.paymentMethod());
        sale.setAmountPaid(dto.amountPaid());

        return sale;
    }

    public SaleItem toItemEntity(SaleItem item) {

        SaleItem saleItem = new SaleItem();

        saleItem.setProductId(item.getProductId());
        saleItem.setProductName(item.getProductName());
        saleItem.setSku(item.getSku());
        saleItem.setQuantity(item.getQuantity());
        saleItem.setUnitPrice(item.getUnitPrice());
        saleItem.setTotalPrice(item.getTotalPrice());

        return saleItem;
    }
}