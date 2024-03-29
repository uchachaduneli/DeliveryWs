package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicesRepository extends JpaRepository<Services, Integer> {
    Page<Services> findByNameAndDeleted(String name, Pageable paging, Integer deleted);

    Page<Services> findByIdAndDeleted(Integer id, Pageable paging, Integer deleted);
}
