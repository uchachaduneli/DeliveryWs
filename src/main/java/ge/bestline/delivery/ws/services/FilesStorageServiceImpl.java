package ge.bestline.delivery.ws.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Service
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

    @Override
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

    @Override
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

    @Override
    public boolean delete(String fileName) throws IOException {
        return Files.deleteIfExists(Path.of(uploadsPath + "//" + fileName));
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(Paths.get(uploadsPath), 1).filter(path -> !path.equals(Paths.get(uploadsPath))).map(Paths.get(uploadsPath)::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
