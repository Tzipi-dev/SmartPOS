
package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.SalesReportDTO;

import java.time.LocalDate;

public interface ISalesReportService {

    SalesReportDTO getSalesReport(
            LocalDate startDate,
            LocalDate endDate
    );
}
