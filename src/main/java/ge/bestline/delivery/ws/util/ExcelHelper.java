package ge.bestline.delivery.ws.util;

import ge.bestline.delivery.ws.dto.ParcelStatusWithReasonsDTO;
import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.entities.Contact;
import ge.bestline.delivery.ws.entities.ParcelStatusReason;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
public class ExcelHelper {

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm");

    private final List<String> cityFieldsToExport = new ArrayList<String>(Arrays.asList(
            new String("ID,NAME,CODE,ZONE").split(",", -1)));
    private final List<String> contactFieldsToExport = new ArrayList<String>(Arrays.asList(
            new String("NAME,IDENT #,ADDRESS,TARIFF").split(",", -1)));
    private final List<String> parcelStatusFieldsToExport = new ArrayList<String>(Arrays.asList(
            new String("ინდექსი,კოდი,სახელი,ჩანაწერის თარიღი,კატეგორია,გზავნილის სტატუსი ჩექპოინტზე").split(",", -1)));
//    private final List<String> statusReasonsFieldsToExport = new ArrayList<String>(Arrays.asList(
//            new String("ინდექსი,კოდი,სახელი,ჩანაწერის თარიღი,კატეგორია,გზავნილის სტატუსი ჩექპოინტზე").split(",", -1)));

    private void createHeaderRow(List<String> headersArrList, Sheet sheet, CellStyle headerCellStyle, int rowIndex, int startColIndx) {
        Row row = sheet.createRow(rowIndex);
        for (int ind = 0; ind < parcelStatusFieldsToExport.size(); ind++) {
            Cell cell = row.createCell(ind + startColIndx);
            cell.setCellValue(parcelStatusFieldsToExport.get(ind));
            cell.setCellStyle(headerCellStyle);
        }
    }

    public ByteArrayInputStream parcelStatusesWithReasonsToExcelFile(List<ParcelStatusWithReasonsDTO> objectslist) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setBorderTop(BorderStyle.THIN);
            headerCellStyle.setBorderBottom(BorderStyle.THIN);
            headerCellStyle.setBorderLeft(BorderStyle.THIN);
            headerCellStyle.setBorderRight(BorderStyle.THIN);
            final int[] i = {0};
            objectslist.forEach(status -> {
                createHeaderRow(parcelStatusFieldsToExport, sheet, headerCellStyle, i[0], 0);
                i[0] += 1;
                Row dataRow = sheet.createRow(i[0]);
                dataRow.createCell(0).setCellValue(status.getStatus().getId());
                dataRow.createCell(1).setCellValue(ifNull(status.getStatus().getCode()));
                dataRow.createCell(2).setCellValue(ifNull(status.getStatus().getName()));
                dataRow.createCell(3).setCellValue(dateFormatter.format(status.getStatus().getCreatedTime()));
                dataRow.createCell(4).setCellValue(ifNull(status.getStatus().getCategory()));
                dataRow.createCell(5).setCellValue(ifNull(status.getStatus().getParcelStatusOnChekpoint()));
                i[0] += 1;
                dataRow = sheet.createRow(i[0]);// empty row for spacing

                if (!status.getReasonList().isEmpty()) {
                    i[0] += 1;
                    createHeaderRow(parcelStatusFieldsToExport, sheet, headerCellStyle, i[0], 1);
                    int j = i[0];

                    for (ParcelStatusReason sr : status.getReasonList()) {
                        i[0] += 1;
                        dataRow = sheet.createRow(i[0]);
                        dataRow.createCell(1).setCellValue(sr.getId());
                        dataRow.createCell(2).setCellValue(ifNull(sr.getCode()));
                        dataRow.createCell(3).setCellValue(ifNull(sr.getName()));
                        dataRow.createCell(4).setCellValue(dateFormatter.format(sr.getCreatedTime()));
                        dataRow.createCell(5).setCellValue(ifNull(sr.getCategory()));
                        dataRow.createCell(6).setCellValue(ifNull(sr.getParcelStatusOnChekpoint()));
                    }
                    sheet.groupRow(j, i[0]);
                }
                i[0] += 1;
                dataRow = sheet.createRow(i[0]);// empty 2 rows for spacing
                i[0] += 1;
                dataRow = sheet.createRow(i[0]);
            });

            // Making size of column auto resize to fit with data
            for (int j = 0; j <= parcelStatusFieldsToExport.size(); j++) {
                sheet.autoSizeColumn(j);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            log.error("Excell Generation Process Failed ", ex);
            return null;
        }
    }

    public ByteArrayInputStream contactsToExcelFile(List<Contact> objectslist) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < contactFieldsToExport.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(contactFieldsToExport.get(i));
                cell.setCellStyle(headerCellStyle);
            }
            final int[] i = {0};
            objectslist.forEach(obj -> {
                i[0] += 1;
                Row dataRow = sheet.createRow(i[0]);
                dataRow.createCell(0).setCellValue(ifNull(obj.getName()));
                dataRow.createCell(1).setCellValue(ifNull(obj.getIdentNumber()));
                dataRow.createCell(2).setCellValue(ifNull(obj.getMainAddress().getStreet())
                        + " " + ifNull(obj.getMainAddress().getAppartmentDetails()));
                dataRow.createCell(3).setCellValue(ifNull(obj.getTariff().getName()));
            });

            // Making size of column auto resize to fit with data
            for (int j = 0; j <= contactFieldsToExport.size(); j++) {
                sheet.autoSizeColumn(j);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            log.error("Excell Generation Process Failed ", ex);
            return null;
        }
    }

    public ByteArrayInputStream citiesToExcelFile(List<City> objectslist) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            Row row = sheet.createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (int i = 0; i < cityFieldsToExport.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(cityFieldsToExport.get(i));
                cell.setCellStyle(headerCellStyle);
            }

            final int[] i = {0};
            objectslist.forEach(obj -> {
                i[0] += 1;
                Row dataRow = sheet.createRow(i[0]);
                dataRow.createCell(0).setCellValue(obj.getId());
                dataRow.createCell(1).setCellValue(ifNull(obj.getName()));
                dataRow.createCell(2).setCellValue(ifNull(obj.getCode()));
                dataRow.createCell(3).setCellValue(ifNull(obj.getZone().getName()));
            });

            // Making size of column auto resize to fit with data
            for (int j = 0; j <= cityFieldsToExport.size(); j++) {
                sheet.autoSizeColumn(j);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException ex) {
            log.error("Excell Generation Process Failed ", ex);
            return null;
        }
    }

    private String ifNull(Object o) {
        if (Objects.isNull(o)) {
            return "";
        }
        return o.toString();
    }
}
