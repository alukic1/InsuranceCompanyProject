package etfbl.sni.insurance_company.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "insurance_policy", indexes = {
        @Index(name = "fk_insurancepolicy_user_idx", columnList = "iduser"),
        @Index(name = "fk_insurancepolicy_type_idx", columnList = "idtype")
})
public class InsurancePolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idinsurance_policy", nullable = false)
    private Long id;

    @Column(name = "start_date", nullable = true)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = true)
    private LocalDate endDate;

    @Column(name = "price_paid", nullable = true)
    private BigDecimal pricePaid;

    @Column(name = "pdf_url", nullable = true)
    private String pdfUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iduser", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtype", nullable = false)
    private InsuranceType type;
}