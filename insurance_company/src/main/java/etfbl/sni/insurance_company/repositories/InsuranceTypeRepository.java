package etfbl.sni.insurance_company.repositories;

import etfbl.sni.insurance_company.entities.InsuranceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceTypeRepository extends JpaRepository<InsuranceType, Long> {
}
