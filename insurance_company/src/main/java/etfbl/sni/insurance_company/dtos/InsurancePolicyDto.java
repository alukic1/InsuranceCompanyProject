package etfbl.sni.insurance_company.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InsurancePolicyDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal pricePaid;
    private InsuranceTypeDto type;
}
