package etfbl.sni.insurance_company.mappers;

import etfbl.sni.insurance_company.dtos.InsurancePolicyDto;
import etfbl.sni.insurance_company.entities.InsurancePolicy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InsurancePolicyMapper {
    InsurancePolicyDto toInsurancePolicyDto(InsurancePolicy insurancePolicy);
}
