package com.minierp.mini_erp.controllers;

import com.minierp.mini_erp.services.ReportService;
import com.minierp.mini_erp.entities.Product;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Düşük stoklu ürünler için Excel (XLSX) raporu döner.
     * Tarayıcıda indirme tetiklenmesi için Content-Disposition: attachment kullanılır.
     */
    @GetMapping(value = "/low-stock/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> getLowStockExcelReport() {
        List<Product> lowStockProducts = reportService.getLowStockProducts();
        if (lowStockProducts.isEmpty()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Tedarik gerektiren ürün bulunmamaktadır".getBytes(StandardCharsets.UTF_8));
        }

        byte[] file = reportService.generateLowStockExcelReport(lowStockProducts);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "low-stock-products.xlsx");
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }

    /**
     * Düşük stoklu ürünler için PDF raporu döner.
     */
    @GetMapping(value = "/low-stock/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> getLowStockPdfReport() {
        List<Product> lowStockProducts = reportService.getLowStockProducts();
        if (lowStockProducts.isEmpty()) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Tedarik gerektiren ürün bulunmamaktadır".getBytes(StandardCharsets.UTF_8));
        }

        byte[] file = reportService.generateLowStockPdfReport();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "low-stock-products.pdf");
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }
}

