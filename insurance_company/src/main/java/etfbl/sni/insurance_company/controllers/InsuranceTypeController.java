package etfbl.sni.insurance_company.controllers;

import etfbl.sni.insurance_company.dtos.CreateInsuranceTypeDto;
import etfbl.sni.insurance_company.entities.InsuranceType;
import etfbl.sni.insurance_company.repositories.InsuranceTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/insurance-types")
@AllArgsConstructor
public class InsuranceTypeController {

    private final InsuranceTypeRepository insuranceTypeRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public List<InsuranceType> getAllInsuranceTypes() {
        return insuranceTypeRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<InsuranceType> getInsuranceTypeById(@PathVariable Long id) {
        var insuranceType = insuranceTypeRepository.findById(id).orElse(null);
        if (insuranceType == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(insuranceType);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<InsuranceType> createInsuranceType(@RequestBody CreateInsuranceTypeDto createInsuranceTypeDto) {

        InsuranceType insuranceType = new InsuranceType();
        insuranceType.setName(createInsuranceTypeDto.getName());
        insuranceType.setDescription(createInsuranceTypeDto.getDescription());
        insuranceType.setPrice(createInsuranceTypeDto.getPrice());

        insuranceTypeRepository.save(insuranceType);
        return ResponseEntity.ok(insuranceType);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<InsuranceType> updateInsuranceType(@PathVariable Long id, @RequestBody CreateInsuranceTypeDto insuranceType) {

        var foundInsuranceType = insuranceTypeRepository.findById(id).orElse(null);

        if (foundInsuranceType == null) {
        return ResponseEntity.notFound().build();}

        foundInsuranceType.setName(insuranceType.getName());
        foundInsuranceType.setDescription(insuranceType.getDescription());
        foundInsuranceType.setPrice(insuranceType.getPrice());
        insuranceTypeRepository.save(foundInsuranceType);

        return ResponseEntity.ok(foundInsuranceType);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInsuranceType(@PathVariable Long id) {
        var insuranceType = insuranceTypeRepository.findById(id).orElse(null);
        if (insuranceType == null) {
            return ResponseEntity.notFound().build();
        }
        insuranceTypeRepository.delete(insuranceType);
        return ResponseEntity.noContent().build();
    }
}
