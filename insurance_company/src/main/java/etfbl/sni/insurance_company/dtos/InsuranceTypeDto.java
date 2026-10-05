package etfbl.sni.insurance_company.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InsuranceTypeDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
}
