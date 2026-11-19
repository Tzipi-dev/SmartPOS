
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.InventoryReportDTO;
import SmartPOS_Server.interfaces.IInventoryReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class InventoryReportController {

    private final IInventoryReportService inventoryReportService;

    public InventoryReportController(
            IInventoryReportService inventoryReportService) {

        this.inventoryReportService = inventoryReportService;
    }

    @GetMapping("/inventory")
    public InventoryReportDTO getInventoryReport(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return inventoryReportService.getInventoryReport(
                startDate,
                endDate
        );
    }
}
