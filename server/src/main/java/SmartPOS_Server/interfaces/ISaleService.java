package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.SaleRequestDTO;
import SmartPOS_Server.models.Sale;

import java.util.List;

public interface ISaleService {

    List<Sale> getAllSales();

    Sale getSaleById(String id);

    List<Sale> getSalesByCustomer(String customerId);

    Sale createSale(SaleRequestDTO dto);

}