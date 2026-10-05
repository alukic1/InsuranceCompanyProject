package etfbl.sni.insurance_company.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyInsuranceRequest {
    private Long insurance_type_id;
    private LocalDate start_date;
    private LocalDate end_date;
}
