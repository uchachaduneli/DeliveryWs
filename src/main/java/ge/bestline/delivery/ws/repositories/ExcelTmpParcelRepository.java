package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.ExcelTmpParcel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExcelTmpParcelRepository extends JpaRepository<ExcelTmpParcel, Integer> {
    List<ExcelTmpParcel> findByAuthorId(Integer authorId);
}
