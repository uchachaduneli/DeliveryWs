package ge.bestline.delivery.ws.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import ge.bestline.delivery.ws.entities.Invoice;
import ge.bestline.delivery.ws.entities.Parcel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Log4j2
@Service
public class PDFService {

    private final FilesStorageService storageService;
    @Value("${uploadsPath}")
    private String uploadsPath;
    private Font font12;
    private Font font12Bold;
    private Font font14;
    private Font font14Bold;
    private Font font16;
    private Font font16Bold;

    public PDFService(FilesStorageService storageService) {
        this.storageService = storageService;
    }

    @PostConstruct
    private void loadFont() {
        try {
            BaseFont baseFont3 = BaseFont.createFont(Paths.get(PDFService.class.getResource("/fonts/dejavu.ttf").toURI()).toFile().getAbsolutePath()
                    , BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            font12 = new Font(baseFont3, 12);
            font12Bold = new Font(baseFont3, 12);
            font12Bold.setStyle(Font.BOLD);
            font14 = new Font(baseFont3, 14);
            font14Bold = new Font(baseFont3, 14);
            font14Bold.setStyle(Font.BOLD);
            font16 = new Font(baseFont3, 16);
            font16Bold = new Font(baseFont3, 16);
            font16Bold.setStyle(Font.BOLD);
            font12.setColor(BaseColor.GRAY);
            font14.setColor(BaseColor.GRAY);
            font16.setColor(BaseColor.GRAY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String generateInvoice(Invoice invoice) throws DocumentException, IOException, URISyntaxException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (!Files.exists(Paths.get(uploadsPath + File.separator + "Invoices" + File.separator))) {
            Files.createDirectory(Paths.get(uploadsPath + File.separator + "Invoices" + File.separator));
        }
        String fileName = invoice.getId() + "-" + new SimpleDateFormat("dd_MM_yyyy-HH_mm_ss").format(new Date()) + ".pdf";
        String pdfPath = uploadsPath + File.separator + "Invoices" + File.separator + fileName;
        Document document = new Document(PageSize.A4, 10, 10, 10, 10);
        document.addTitle("Invoice#" + invoice.getId());
        document.addSubject("Invoice For " + invoice.getName() + " " + invoice.getIdentNumber());
        document.addKeywords("Invoice");
        document.addAuthor("Express Line");
        document.addCreator("Express Line Management System");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        document.add(new Chunk(""));

        PdfPTable table = new PdfPTable(2); // 2 columns.
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table
        //Set Column widths
        float[] columnWidths = {1f, 1f};
        table.setWidths(columnWidths);

        PdfPCell cell1 = new PdfPCell(new Paragraph(" ინვოისი #" + invoice.getId(), font16Bold));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPaddingLeft(10);
        cell1.setPaddingBottom(20);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Image image = Image.getInstance(Paths.get(PDFService.class.getResource("/images/logo.png").toURI()).toFile().getAbsolutePath());
        image.scaleAbsolute(100, 50);

        PdfPCell cell2 = new PdfPCell(image);
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPaddingLeft(10);
        cell1.setPaddingBottom(20);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph(simpleDateFormat.format(new Date()), font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("შემსრულებელი", font14Bold));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph("დამკვეთი", font14Bold));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("შ.პ.ს. \"ექსპრეს ხაზი\"", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph(invoice.getName(), font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("ს/კ 405178258", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph("ს/კ " + invoice.getIdentNumber(), font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("დ.უზნაძის 4", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

//        cell2 = new PdfPCell(new Paragraph(invoice.getPayerAddress(), font12));
        cell2 = new PdfPCell(new Paragraph("", font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("ს.ს თიბისი ბანკი - TBCBGE22", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph("", font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("ა.ნ. GE64TB7713936080100010", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph("", font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("ს.ს საქართველოს ბანკი - BAGAGE22", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph("", font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        cell1 = new PdfPCell(new Paragraph("ა.ნ. GE54BG0000000498958429", font12));
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph("", font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        document.add(table);
        // companies description finishes here and parcels table starts

        Paragraph emptyParagraph = new Paragraph();
        addEmptyLine(emptyParagraph, 2);
        document.add(emptyParagraph);


        table = new PdfPTable(7);
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table
        //Set Column widths
        float[] colWidths = {1f, 1f, 1f, 1f, 1f, 1f, 1f};
        table.setWidths(colWidths);


        table.addCell(getParcelsTableCell("#", true, font14Bold));
        table.addCell(getParcelsTableCell("თარიღი", true, font14Bold));
        table.addCell(getParcelsTableCell("გზავნილის #", true, font14Bold));
        table.addCell(getParcelsTableCell("გამგზავნი ქალაქი", true, font14Bold));
        table.addCell(getParcelsTableCell("მიმღები ქალაქი", true, font14Bold));
        table.addCell(getParcelsTableCell("წონა", true, font14Bold));
        table.addCell(getParcelsTableCell("ფასი", true, font14Bold));
        table.setHeaderRows(1);

        Double parcelPriceSum = 0.0;
        int i = 1;
        for (Parcel p : invoice.getParcels()) {
            table.addCell(getParcelsTableCell(i + "", false, font12Bold));
            table.addCell(getParcelsTableCell(p.getCreatedTime() != null ? simpleDateFormat.format(p.getCreatedTime()) : "-", false, font12));
            table.addCell(getParcelsTableCell(p.getBarCode(), false, font12Bold));
            table.addCell(getParcelsTableCell(p.getSenderCity() != null ? p.getSenderCity().getName() : "-", false, font12));
            table.addCell(getParcelsTableCell(p.getReceiverCity() != null ? p.getReceiverCity().getName() : "", false, font12));
            table.addCell(getParcelsTableCell(p.getWeight() + "", false, font12));
            table.addCell(getParcelsTableCell(p.getTotalPrice() + "", false, font12));
            if (p.getTotalPrice() != null)
                parcelPriceSum += p.getTotalPrice();
            i++;
        }
        document.add(table);
        document.add(emptyParagraph);

        table = new PdfPTable(2);
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table
        table.setWidths(columnWidths);

//        File file = ResourceUtils.getFile("classpath:/images/sign.jpg");
        image = Image.getInstance(Paths.get(PDFService.class.getResource("/images/sign.jpg").toURI()).toFile().getAbsolutePath());
        image.scaleAbsolute(100, 100);

        cell1 = new PdfPCell(image);
        cell1.setBorderColor(BaseColor.WHITE);
        cell1.setPadding(10);
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);

        cell2 = new PdfPCell(new Paragraph("სულ ლარი: " + parcelPriceSum, font12));
        cell2.setBorderColor(BaseColor.WHITE);
        cell2.setPadding(10);
        cell2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);

        table.addCell(cell1);
        table.addCell(cell2);

        document.add(table);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineWidth(1);
        document.add(lineSeparator);
        Paragraph p;//= new Paragraph();
        //p.setAlignment(Element.ALIGN_CENTER);
//        p.add(lineSeparator);
//        document.add(p);
        document.add(new Chunk(""));
        document.add(new Chunk(""));
        p = new Paragraph("გთხოვთ ანგარიშსწორება მოახდინოთ ხუთი საბანკო დღის განმავლობაში, " +
                "გადმორიცხვისას მიუთითეთ ინვოისის ნომერი, " +
                "გმადლობთ თანამშრომლობისათვის", font12);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);


        document.close();
        writer.close();

        return fileName;
    }

    private PdfPCell getParcelsTableCell(String text, boolean hasBackground, Font font) {
        PdfPCell c1 = new PdfPCell(new Paragraph(text, font));
        if (hasBackground) {
            c1.setBackgroundColor(BaseColor.LIGHT_GRAY);
        }
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        c1.setBorderColor(BaseColor.BLACK);
        c1.setPadding(4);
        c1.setBorderWidth(1);
        return c1;
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}
