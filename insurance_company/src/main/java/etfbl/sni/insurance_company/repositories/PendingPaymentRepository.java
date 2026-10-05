package etfbl.sni.insurance_company.repositories;


import etfbl.sni.insurance_company.entities.PendingPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingPaymentRepository extends JpaRepository<PendingPayment, Long> {
    Optional<PendingPayment> findBySessionId(String sessionId);
}
