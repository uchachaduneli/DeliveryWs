package ge.bestline.delivery.ws.services;

import ge.bestline.delivery.ws.entities.City;
import ge.bestline.delivery.ws.entities.ExcelTmpParcel;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Log4j2
@Service
@Data
public class FilesStorageServiceImpl implements FilesStorageService {
    @Value("${uploadsPath}")
    private String uploadsPath;

    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(Paths.get(uploadsPath))) {
                Files.createDirectory(Paths.get(uploadsPath));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
    }

    public String save(MultipartFile file, Integer parcelId) {
        try {
            String newFileName = parcelId != null ?
                    parcelId + "_" + new Date().getTime() + "_" + file.getOriginalFilename()
                    : new Date().getTime() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), Paths.get(uploadsPath).resolve(newFileName));
            return newFileName;
        } catch (Exception e) {
            throw new RuntimeException("Could not upload the file. Error: " + e.getMessage());
        }
    }

    public Resource load(String filename) {
        try {
            Path file = Paths.get(uploadsPath).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public boolean delete(String fileName) throws IOException {
        return Files.deleteIfExists(Path.of(uploadsPath + "//" + fileName));
    }

    public Stream<Path> loadAll() {
        try {
            return Files.walk(Paths.get(uploadsPath), 1).filter(path -> !path.equals(Paths.get(uploadsPath))).map(Paths.get(uploadsPath)::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    public List<ExcelTmpParcel> convertExcelToParcelList(MultipartFile file) {
        int rowNum = 0;
        int colIndx = 0;
        ExcelTmpParcel obj = null;
        try {
            List<ExcelTmpParcel> res = new ArrayList<>();
            FileInputStream inputStream = (FileInputStream) file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();


            for (Row row : firstSheet) {
                if (row.getRowNum() > 0) {
                    rowNum = row.getRowNum();
                    obj = new ExcelTmpParcel();
                    obj.setRowIndex(rowNum);
                    for (Cell cell : row) {
                        colIndx = cell.getColumnIndex() + 1;
                        switch (cell.getColumnIndex()) {
                            case 0:
                                obj.setReceiverName(formatter.formatCellValue(cell));
                                break;
                            case 1:
                                obj.setReceiverIdentNumber(formatter.formatCellValue(cell));
                                break;
                            case 2:
                                obj.setReceiverContactPerson(formatter.formatCellValue(cell));
                                break;
                            case 3:
                                obj.setReceiverAddress(formatter.formatCellValue(cell));
                                break;
                            case 4:
                                obj.setReceiverPhone(formatter.formatCellValue(cell));
                                break;
                            case 5:
                                City city = new City();
                                city.setId(Integer.valueOf(formatter.formatCellValue(cell)));
                                obj.setReceiverCity(city);
                                break;
                            case 6:
                                obj.setComment(formatter.formatCellValue(cell));
                                break;
                            case 7:
                                obj.setCount(Integer.valueOf(formatter.formatCellValue(cell)));
                                break;
                            case 8:
                                obj.setWeight(Double.valueOf(formatter.formatCellValue(cell)));
                                break;
                            case 9:
                                obj.setContent(formatter.formatCellValue(cell));
                                break;
                            default:
                                break;
                        }
                    }
                    res.add(obj);
                }
            }
            return res;
        } catch (Exception e) {
            log.error("Error Occured While Parsing Excel file: " + file.getOriginalFilename(), e);
            throw new RuntimeException("Error Occured While Parsing Excel file At Row: " + rowNum + "  and Column: " + colIndx);
        }
    }
}
