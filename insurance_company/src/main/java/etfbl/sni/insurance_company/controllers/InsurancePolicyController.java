package etfbl.sni.insurance_company.controllers;

import etfbl.sni.insurance_company.dtos.InsurancePolicyDto;
import etfbl.sni.insurance_company.mappers.InsurancePolicyMapper;
import etfbl.sni.insurance_company.repositories.InsurancePolicyRepository;
import etfbl.sni.insurance_company.repositories.UserRepository;
import etfbl.sni.insurance_company.security.JwtService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insurance-policies")
@AllArgsConstructor
public class InsurancePolicyController {

    private final InsurancePolicyRepository insurancePolicyRepository;
    private final InsurancePolicyMapper insurancePolicyMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @GetMapping("/my-policies")
    public List<InsurancePolicyDto> getMyPolicies(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username);
         Long userId= user.get().getId();

        return insurancePolicyRepository.findAllByUserId(userId).stream().map(insurancePolicyMapper::toInsurancePolicyDto).toList();
    }
}
