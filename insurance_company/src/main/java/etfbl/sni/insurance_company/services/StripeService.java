package etfbl.sni.insurance_company.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import etfbl.sni.insurance_company.dtos.BuyInsuranceRequest;
import etfbl.sni.insurance_company.dtos.StripeResponse;
import etfbl.sni.insurance_company.entities.InsuranceType;
import etfbl.sni.insurance_company.entities.PendingPayment;
import etfbl.sni.insurance_company.repositories.InsuranceTypeRepository;
import etfbl.sni.insurance_company.repositories.PendingPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
public class StripeService {

    private final InsuranceTypeRepository insuranceTypeRepository;
    private final PendingPaymentRepository pendingPaymentRepository;
    @Value("${stripe.secretKey}")
    private String secretKey;

    @Value("${stripe.currency}")
    private String currency;

    @Value("${stripe.successUrl}")
    private String successUrl;
    @Value("${stripe.cancelUrl}")
    private String cancelUrl;

    @Autowired
    public StripeService(InsuranceTypeRepository insuranceTypeRepository, PendingPaymentRepository pendingPaymentRepository) {
        this.insuranceTypeRepository = insuranceTypeRepository;
        this.pendingPaymentRepository = pendingPaymentRepository;
    }

    public StripeResponse checkout (BuyInsuranceRequest request, Long userId){
        Stripe.apiKey = secretKey;

        InsuranceType insuranceType = insuranceTypeRepository.findById(request.getInsurance_type_id()).orElseThrow(() -> new IllegalArgumentException("Invalid insurance type"));
        long amountInCents = insuranceType.getPrice().multiply(BigDecimal.valueOf(100)).longValue();
        long days = ChronoUnit.DAYS.between(request.getStart_date(), request.getEnd_date())+1;

        SessionCreateParams.LineItem.PriceData.ProductData  productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder().setName(insuranceType.getName()).build();

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder().setCurrency(currency)
                .setUnitAmount(amountInCents)
                .setProductData(productData).build();

        SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                .setQuantity(days)
                .setPriceData(priceData).build();

        SessionCreateParams params = SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(lineItem)
                .build();

        Session session = null;

        try{
            session=Session.create(params);
        }
        catch(StripeException e){
            //log
            e.printStackTrace();
        }

        PendingPayment pendingPayment = new PendingPayment();
        pendingPayment.setAmount(insuranceType.getPrice().multiply(BigDecimal.valueOf(days)));
        pendingPayment.setInsuranceTypeId(insuranceType.getId());
        pendingPayment.setSessionId(session.getId());
        pendingPayment.setUserId(userId);
        pendingPayment.setStartDate(request.getStart_date());
        pendingPayment.setEndDate(request.getEnd_date());
        pendingPaymentRepository.save(pendingPayment);

        return new StripeResponse("SUCCESS", "Redirect to Stripe", session.getId(), session.getUrl());
    }
}
