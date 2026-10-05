package etfbl.sni.insurance_company.repositories;

import etfbl.sni.insurance_company.entities.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findByUsername(String username);

    void deleteAllByUsername(String username);
}
