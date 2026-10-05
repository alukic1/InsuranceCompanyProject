package etfbl.sni.insurance_company.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
public class PendingPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    private Long insuranceTypeId;

    private BigDecimal amount;

    private Long userId;

    private Instant createdAt = Instant.now();

    private LocalDate startDate;

    private LocalDate endDate;
}