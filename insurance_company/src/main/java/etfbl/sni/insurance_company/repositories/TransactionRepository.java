package etfbl.sni.insurance_company.repositories;

import etfbl.sni.insurance_company.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
