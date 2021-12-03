package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Files;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilesRepository extends JpaRepository<Files, Integer> {
    List<Files> findByParcelId(Integer id);

    Files findByName(String filename);
}
