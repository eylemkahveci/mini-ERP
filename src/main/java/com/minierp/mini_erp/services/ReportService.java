package com.minierp.mini_erp.services;

import com.lowagie.text.pdf.BaseFont;
import com.minierp.mini_erp.entities.Product;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    private final ProductService productService;

    public ReportService(ProductService productService) {
        this.productService = productService;
    }


     // Düşük stoklu ürünler için basit bir Excel (XLSX) raporu üretir.
    public byte[] generateLowStockExcelReport() {
        List<Product> lowStockProducts = productService.getLowStockProducts();

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Düşük Stoklu Ürünler");

            // Başlık satırı
            Row header = sheet.createRow(0);
            createCell(header, 0, "Ürün ID");
            createCell(header, 1, "Ürün Adı");
            createCell(header, 2, "Kategori");
            createCell(header, 3, "Miktar");
            createCell(header, 4, "Kritik Seviye");

            int rowIdx = 1;
            for (Product p : lowStockProducts) {
                Row row = sheet.createRow(rowIdx++);
                createCell(row, 0, p.getPId() != null ? p.getPId().toString() : "");
                createCell(row, 1, p.getName());
                createCell(row, 2, p.getCategory() != null ? p.getCategory().getName() : "");
                createCell(row, 3, p.getQuantity() != null ? p.getQuantity().toString() : "");
                createCell(row, 4, p.getCriticalStockLevel() != null ? p.getCriticalStockLevel().toString() : "");
            }

            // Sütun genişliklerini otomatik ayarla
            for (int i = 0; i <= 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Excel raporu oluşturulurken hata oluştu", e);
        }
    }

    private void createCell(Row row, int columnIndex, String value) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value != null ? value : "");
    }


     // Düşük stoklu ürünler için basit bir PDF raporu
    public byte[] generateLowStockPdfReport() {
        List<Product> lowStockProducts = productService.getLowStockProducts();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            //  Türkçe Karakter Desteği
            BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(bf, 14, Font.BOLD);
            Font bodyFont = new Font(bf, 10, Font.NORMAL);

            Paragraph title = new Paragraph("Düşük Stoklu Ürünler Raporu", titleFont);
            title.setSpacingAfter(15);
            document.add(title);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100f);
            table.setSpacingBefore(10);

            // Başlık hücreleri
            table.addCell(createPdfCell("Ürün ID", true));
            table.addCell(createPdfCell("Ürün Adı", true));
            table.addCell(createPdfCell("Kategori", true));
            table.addCell(createPdfCell("Miktar", true));
            table.addCell(createPdfCell("Kritik Seviye", true));

            for (Product p : lowStockProducts) {
                table.addCell(createPdfCell(p.getPId() != null ? p.getPId().toString() : "", false));
                table.addCell(createPdfCell(p.getName() != null ? p.getName() : "", false));
                table.addCell(createPdfCell(p.getCategory() != null ? p.getCategory().getName() : "", false));
                table.addCell(createPdfCell(p.getQuantity() != null ? p.getQuantity().toString() : "", false));
                table.addCell(createPdfCell(p.getCriticalStockLevel() != null ? p.getCriticalStockLevel().toString() : "", false));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("PDF raporu oluşturulurken hata oluştu", e);
        }
    }

    private PdfPCell createPdfCell(String text, boolean header) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : ""));
        if (header) {
            cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);        }
        return cell;
    }
}

