
package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.SaleReturnRequestDTO;
import SmartPOS_Server.models.SaleReturn;

import java.util.List;

public interface ISaleReturnService {

    List<SaleReturn> getAllReturns();

    SaleReturn getReturnById(String id);

    List<SaleReturn> getReturnsBySale(String saleId);

    SaleReturn createReturn(SaleReturnRequestDTO dto);
}
