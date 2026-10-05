package etfbl.sni.insurance_company.dtos;

import lombok.Data;

@Data
public class VerificationCodeRequest {
    private String username;
    private String code;
}
