package etfbl.sni.insurance_company.repositories;

import etfbl.sni.insurance_company.entities.InsurancePolicy;
import etfbl.sni.insurance_company.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {

    List<InsurancePolicy> findAllByUser(User user);

    List<InsurancePolicy> findAllByUserId(Long id);
}
