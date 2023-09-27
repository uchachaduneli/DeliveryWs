package ge.bestline.delivery.ws.repositories;

import ge.bestline.delivery.ws.entities.TwoFaCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TwoFaCodeRepository extends JpaRepository<TwoFaCode, Integer> {
    Optional<TwoFaCode> findByPhoneAndCodeAndExpiredAndUsed(String phone, String code, boolean b, boolean b1);
}
