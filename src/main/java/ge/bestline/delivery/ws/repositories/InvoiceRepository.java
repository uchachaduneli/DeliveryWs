package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
}
