package etfbl.sni.insurance_company.controllers;

import com.stripe.model.Event;
import com.stripe.net.Webhook;
import etfbl.sni.insurance_company.dtos.BuyInsuranceRequest;
import etfbl.sni.insurance_company.dtos.StripeResponse;
import etfbl.sni.insurance_company.entities.*;
import etfbl.sni.insurance_company.repositories.*;
import etfbl.sni.insurance_company.security.JwtService;
import etfbl.sni.insurance_company.services.EmailService;
import etfbl.sni.insurance_company.services.PdfGeneratorService;
import etfbl.sni.insurance_company.services.SiemService;
import etfbl.sni.insurance_company.services.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@RestController
@RequestMapping("/api/stripe")
@RequiredArgsConstructor
public class CheckoutController {

    private final PendingPaymentRepository pendingPaymentRepository;
    private final UserRepository userRepository;
    private final InsuranceTypeRepository insuranceTypeRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final TransactionRepository transactionRepository;
    private final StripeService stripeService;
    private final PdfGeneratorService pdfGeneratorService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final SiemService siemService;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Value("${siem.max.unsuspicious.amount}")
    private BigDecimal maxUnsuspicious;



    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkout(@RequestBody BuyInsuranceRequest request, @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        var user = userRepository.findByUsername(username);
        Long userId= user.get().getId();

        BigDecimal amount = calculateAmount(request);

        if (amount.compareTo(maxUnsuspicious) > 0) {
            siemService.logSecurityEvent("Pokušaj sumnjive uplate! " +
                    "Korisnik: " + username +
                    ", Iznos: " + amount +
                    ", Tip osiguranja: " + request.getInsurance_type_id() +
                    ", Period: " + request.getStart_date() + " - " + request.getEnd_date());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        StripeResponse response = stripeService.checkout(request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) throws Exception {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }


        if ("checkout.session.completed".equals(event.getType())) {

            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            JsonObject sessionData = json.getAsJsonObject("data").getAsJsonObject("object");
            String sessionId = sessionData.get("id").getAsString();

            try{
            processSuccessfulPayment(sessionId);}
            catch(NullPointerException e){
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        /*
        else if ("checkout.session.failed".equals(event.getType()) || "checkout.session.expired".equals(event.getType())) {
            JsonObject json = JsonParser.parseString(payload).getAsJsonObject();
            JsonObject sessionData = json.getAsJsonObject("data").getAsJsonObject("object");
            String sessionId = sessionData.get("id").getAsString();

            processFailedPayment(sessionId);

        }
*/
        return ResponseEntity.ok("");
    }

    private BigDecimal calculateAmount(BuyInsuranceRequest request) {

        InsuranceType insuranceType = insuranceTypeRepository.findById(request.getInsurance_type_id()).orElseThrow(() -> new IllegalArgumentException("Invalid insurance type"));
        long days = ChronoUnit.DAYS.between(request.getStart_date(), request.getEnd_date())+1;

        return insuranceType.getPrice().multiply(BigDecimal.valueOf(days));
    }

    private void processFailedPayment(String id) throws Exception {
        PendingPayment pending = pendingPaymentRepository.findBySessionId(id).orElse(null);

        if(pending == null) {
            throw new NullPointerException("Invalid session id");
        }

        User user = userRepository.findById(pending.getUserId()).orElseThrow();
        InsuranceType type = insuranceTypeRepository.findById(pending.getInsuranceTypeId()).orElseThrow();


        InsurancePolicy policy = new InsurancePolicy();
        policy.setType(type);
        policy.setUser(user);
        insurancePolicyRepository.save(policy);

        Transaction transaction = new Transaction();
        transaction.setAmount(pending.getAmount());
        transaction.setIdpolicy(policy);
        transaction.setTime(Instant.now());
        transaction.setPaymentStatus(PaymentStatus.FAILED);
        transactionRepository.save(transaction);
    }


    private void processSuccessfulPayment(String sessionId) {
        PendingPayment pending = pendingPaymentRepository.findBySessionId(sessionId).orElseThrow();

        User user = userRepository.findById(pending.getUserId()).orElseThrow();
        InsuranceType type = insuranceTypeRepository.findById(pending.getInsuranceTypeId()).orElseThrow();

        InsurancePolicy policy = new InsurancePolicy();
        policy.setStartDate(pending.getStartDate());
        policy.setEndDate(pending.getEndDate());
        policy.setPricePaid(pending.getAmount());
        policy.setType(type);
        policy.setUser(user);

        try {
            String pdfPath = pdfGeneratorService.generatePolicyPdf(policy);
            policy.setPdfUrl(pdfPath);
            emailService.sendEmailWithPdfAttachment(user.getEmail(), "AL Osiguranje - Polisa osiguranja"
            ,"Poštovani, Vaša kupovina je uspješno izvršena. U prilogu Vam dostavljamo polisu osiguranja.",
                    new File(pdfPath));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        insurancePolicyRepository.save(policy);

        Transaction transaction = new Transaction();
        transaction.setAmount(pending.getAmount());
        transaction.setIdpolicy(policy);
        transaction.setTime(Instant.now());
        transaction.setPaymentStatus(PaymentStatus.SUCCESS);
        transactionRepository.save(transaction);

        pendingPaymentRepository.delete(pending);
    }

}
