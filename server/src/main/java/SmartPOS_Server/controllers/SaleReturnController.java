
package SmartPOS_Server.controllers;

import SmartPOS_Server.dtos.SaleReturnRequestDTO;
import SmartPOS_Server.interfaces.ISaleReturnService;
import SmartPOS_Server.models.SaleReturn;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sale-returns")
@CrossOrigin(origins = "*")
public class SaleReturnController {

    private final ISaleReturnService returnService;

    public SaleReturnController(ISaleReturnService returnService) {
        this.returnService = returnService;
    }

    @GetMapping
    public ResponseEntity<List<SaleReturn>> getAllReturns() {
        return ResponseEntity.ok(returnService.getAllReturns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleReturn> getReturnById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                returnService.getReturnById(id)
        );
    }

    @GetMapping("/sale/{saleId}")
    public ResponseEntity<List<SaleReturn>> getReturnsBySale(
            @PathVariable String saleId) {

        return ResponseEntity.ok(
                returnService.getReturnsBySale(saleId)
        );
    }

    @PostMapping
    public ResponseEntity<SaleReturn> createReturn(
            @Valid @RequestBody SaleReturnRequestDTO dto) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(returnService.createReturn(dto));
    }
}