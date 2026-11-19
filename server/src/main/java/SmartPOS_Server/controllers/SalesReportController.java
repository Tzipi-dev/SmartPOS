
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.SalesReportDTO;
import SmartPOS_Server.interfaces.ISalesReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class SalesReportController {

    private final ISalesReportService salesReportService;

    public SalesReportController(
            ISalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }

    @GetMapping("/sales")
    public ResponseEntity<SalesReportDTO> getSalesReport(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                salesReportService.getSalesReport(
                        startDate,
                        endDate
                )
        );
    }
}
