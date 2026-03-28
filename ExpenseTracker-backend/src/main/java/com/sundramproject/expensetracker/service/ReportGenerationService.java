package com.sundramproject.expensetracker.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.sundramproject.expensetracker.model.entity.Expense;
import com.sundramproject.expensetracker.model.entity.User;
import com.sundramproject.expensetracker.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportGenerationService {

    private final ExpenseRepository expenseRepository;

    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(1, 41, 112);
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(65, 84, 241);
    private static final DeviceRgb SUCCESS_COLOR = new DeviceRgb(46, 202, 106);
    private static final DeviceRgb DANGER_COLOR = new DeviceRgb(255, 119, 29);
    private static final DeviceRgb LIGHT_BG = new DeviceRgb(246, 249, 255);

    public byte[] generatePdfReport(User user, String reportType, LocalDate from, LocalDate to) throws Exception {

        List<Expense> expenses = getFilteredExpenses(user.getId(), reportType, from, to);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(40, 40, 60, 40);

        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new FooterHandler());

        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        try {
            ClassPathResource logoResource = new ClassPathResource("static/images/eta_logo.png");
            if (logoResource.exists()) {
                InputStream logoStream = logoResource.getInputStream();
                ImageData imageData = ImageDataFactory.create(logoStream.readAllBytes());
                Image logo = new Image(imageData)
                        .setWidth(120)
                        .setHorizontalAlignment(HorizontalAlignment.LEFT);
                headerTable.addCell(new Cell().add(logo).setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE));
            } else {
                headerTable.addCell(new Cell().add(new Paragraph("EXPENSE TRACKER").setBold().setFontSize(20).setFontColor(PRIMARY_COLOR)).setBorder(Border.NO_BORDER));
            }
        } catch (Exception e) {
            headerTable.addCell(new Cell().add(new Paragraph("EXPENSE TRACKER").setBold().setFontSize(20).setFontColor(PRIMARY_COLOR)).setBorder(Border.NO_BORDER));
        }

        Cell infoCell = new Cell().add(new Paragraph(reportType.toUpperCase())
                .setBold().setFontSize(22).setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("Generated for: " + user.getName())
                        .setFontSize(10).setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("Period: " + from.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + " - " + to.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")))
                        .setFontSize(10).setTextAlignment(TextAlignment.RIGHT))
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        headerTable.addCell(infoCell);

        document.add(headerTable);

        double totalIncome = 0;
        double totalExpense = 0;
        for (Expense e : expenses) {
            if ("INCOME".equalsIgnoreCase(e.getType())) totalIncome += e.getAmount();
            else totalExpense += e.getAmount();
        }

        Table summaryTable = new Table(UnitValue.createPercentArray(3))
                .useAllAvailableWidth()
                .setMarginBottom(30);

        summaryTable.addCell(createSummaryCard("TOTAL INCOME", "₹" + String.format("%.2f", totalIncome), SUCCESS_COLOR));
        summaryTable.addCell(createSummaryCard("TOTAL EXPENSE", "₹" + String.format("%.2f", totalExpense), DANGER_COLOR));
        summaryTable.addCell(createSummaryCard("NET BALANCE", "₹" + String.format("%.2f", totalIncome - totalExpense), SECONDARY_COLOR));

        document.add(summaryTable);

        document.add(new Paragraph("Transaction Details")
                .setBold().setFontSize(14).setFontColor(PRIMARY_COLOR).setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 2, 2}))
                .useAllAvailableWidth();

        String[] headers = {"Date", "Category", "Amount", "Type"};
        for (String h : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(h).setBold().setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(PRIMARY_COLOR)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER));
        }

        int rowIdx = 0;
        for (Expense e : expenses) {
            boolean isIncome = "INCOME".equalsIgnoreCase(e.getType());
            DeviceRgb rowBg = (rowIdx % 2 == 0) ? (DeviceRgb) ColorConstants.WHITE : LIGHT_BG;

            table.addCell(new Cell().add(new Paragraph(e.getDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"))))
                    .setBackgroundColor(rowBg).setPadding(5).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(e.getCategory()))
                    .setBackgroundColor(rowBg).setPadding(5));
            table.addCell(new Cell().add(new Paragraph("₹" + String.format("%.2f", e.getAmount())))
                    .setBackgroundColor(rowBg).setPadding(5)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontColor(isIncome ? SUCCESS_COLOR : DANGER_COLOR)
                    .setBold());
            table.addCell(new Cell().add(new Paragraph(e.getType()))
                    .setBackgroundColor(rowBg).setPadding(5).setTextAlignment(TextAlignment.CENTER));

            rowIdx++;
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private Cell createSummaryCard(String title, String value, DeviceRgb color) {
        Table innerTable = new Table(1).useAllAvailableWidth();
        innerTable.addCell(new Cell().add(new Paragraph(title).setFontSize(9).setBold().setFontColor(ColorConstants.GRAY))
                .setBorder(Border.NO_BORDER).setPaddingBottom(2));
        innerTable.addCell(new Cell().add(new Paragraph(value).setFontSize(16).setBold().setFontColor(color))
                .setBorder(Border.NO_BORDER));

        return new Cell().add(innerTable)
                .setBackgroundColor(LIGHT_BG)
                .setPadding(15)
                .setMargin(5)
                .setBorder(Border.NO_BORDER);
    }

    private static class FooterHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            PdfPage page = docEvent.getPage();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
            Canvas canvas = new Canvas(pdfCanvas, pageSize);

            Paragraph footer = new Paragraph("Powered by ExpenseTracker | Generated on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a")))
                    .setFontSize(9)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER);

            canvas.showTextAligned(footer, pageSize.getWidth() / 2, 30, TextAlignment.CENTER);
            
            String pageNum = String.valueOf(pdf.getPageNumber(page));
            canvas.showTextAligned(new Paragraph("Page " + pageNum).setFontSize(9).setFontColor(ColorConstants.GRAY), 
                    pageSize.getWidth() - 40, 30, TextAlignment.RIGHT);
            
            canvas.close();
        }
    }

    public byte[] generateExcelReport(User user, String reportType, LocalDate from, LocalDate to) throws Exception {
        List<Expense> expenses = getFilteredExpenses(user.getId(), reportType, from, to);
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Transactions");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Date");
        headerRow.createCell(1).setCellValue("Category");
        headerRow.createCell(2).setCellValue("Amount");
        headerRow.createCell(3).setCellValue("Type");

        int rowNum = 1;
        for (Expense e : expenses) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.getDate().toString());
            row.createCell(1).setCellValue(e.getCategory());
            row.createCell(2).setCellValue(e.getAmount());
            row.createCell(3).setCellValue(e.getType());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    private List<Expense> getFilteredExpenses(Long userId, String reportType, LocalDate from, LocalDate to) {
        if ("Spending Report".equalsIgnoreCase(reportType)) {
            return expenseRepository.findByUserIdAndTypeAndDateBetween(userId, "EXPENSE", from, to);
        } else if ("Income Report".equalsIgnoreCase(reportType)) {
            return expenseRepository.findByUserIdAndTypeAndDateBetween(userId, "INCOME", from, to);
        } else {
            return expenseRepository.findByUserIdAndDateBetween(userId, from, to);
        }
    }
}

