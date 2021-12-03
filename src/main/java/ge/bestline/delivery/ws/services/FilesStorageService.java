package ge.bestline.delivery.ws.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
    public void init();
    public String save(MultipartFile file, Integer parcelId);
    public Resource load(String filename);
    public boolean delete(String filename) throws IOException;
    public Stream<Path> loadAll();
}
