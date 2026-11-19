
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.ProfitabilityReportDTO;
import SmartPOS_Server.interfaces.IProfitabilityReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports/profitability")
public class ProfitabilityReportController {

    private final IProfitabilityReportService profitabilityReportService;

    public ProfitabilityReportController(
            IProfitabilityReportService profitabilityReportService
    ) {
        this.profitabilityReportService = profitabilityReportService;
    }

    @GetMapping
    public ProfitabilityReportDTO getProfitabilityReport(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return profitabilityReportService.getProfitabilityReport(
                startDate,
                endDate
        );
    }
}
