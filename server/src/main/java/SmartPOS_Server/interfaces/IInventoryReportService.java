
package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.InventoryReportDTO;

import java.time.LocalDate;

public interface IInventoryReportService {

    InventoryReportDTO getInventoryReport(
            LocalDate startDate,
            LocalDate endDate
    );
}
