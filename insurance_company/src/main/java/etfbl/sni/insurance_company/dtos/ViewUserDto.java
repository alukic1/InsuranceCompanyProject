package etfbl.sni.insurance_company.dtos;

import lombok.Data;

@Data
public class ViewUserDto {
    private Long id;
    private String name;
    private String lastname;
    private String nationalIdNumber;
    private String username;
    private String email;
    private String role;
    private boolean blocked;
}
