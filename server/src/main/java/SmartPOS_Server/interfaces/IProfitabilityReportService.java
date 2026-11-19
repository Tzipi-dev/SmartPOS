
package SmartPOS_Server.interfaces;

import SmartPOS_Server.dtos.ProfitabilityReportDTO;
import java.time.LocalDate;

public interface IProfitabilityReportService {

    ProfitabilityReportDTO getProfitabilityReport(
            LocalDate startDate,
            LocalDate endDate
    );
}
