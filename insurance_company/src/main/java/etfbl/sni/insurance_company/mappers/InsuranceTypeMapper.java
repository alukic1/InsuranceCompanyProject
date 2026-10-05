package etfbl.sni.insurance_company.mappers;

import etfbl.sni.insurance_company.dtos.InsuranceTypeDto;
import etfbl.sni.insurance_company.entities.InsuranceType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InsuranceTypeMapper {
    InsuranceTypeDto toInsuranceTypeDto(InsuranceType insuranceType);
}
