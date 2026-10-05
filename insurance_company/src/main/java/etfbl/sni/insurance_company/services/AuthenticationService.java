package etfbl.sni.insurance_company.services;

import etfbl.sni.insurance_company.dtos.AuthenticationRequest;
import etfbl.sni.insurance_company.dtos.AuthenticationResponse;
import etfbl.sni.insurance_company.dtos.RegisterRequest;
import etfbl.sni.insurance_company.dtos.VerificationCodeRequest;
import etfbl.sni.insurance_company.entities.Role;
import etfbl.sni.insurance_company.entities.User;
import etfbl.sni.insurance_company.entities.VerificationCode;
import etfbl.sni.insurance_company.exceptions.UserBlockedException;
import etfbl.sni.insurance_company.repositories.UserRepository;
import etfbl.sni.insurance_company.repositories.VerificationCodeRepository;
import etfbl.sni.insurance_company.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final SiemService siemService;

    @Value("${siem.max.login.failed.attempts}")
    private int max_login_attempts;

    @Value("${siem.max.code.failed.attempts}")
    private int max_code_attempts;

    public AuthenticationResponse register(RegisterRequest request, Role role) {

        var userExists = userRepository.findByUsername(request.getUsername());
        if (!userExists.equals(Optional.empty()))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        userExists = userRepository.findByEmail(request.getEmail());
        if (!userExists.equals(Optional.empty())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        var user = User.builder().name(request.getName()).lastname(request.getLastName())
                        .nationalIdNumber(request.getNationalIdNumber()).username(request.getUsername())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(role).build();

        userRepository.save(user);

        return AuthenticationResponse.builder().build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        var userOptional = userRepository.findByUsername(request.getUsername());

        if(userOptional.isEmpty()){
            throw new BadCredentialsException("Invalid username");
        }
        var user = userOptional.get();

        if(user.isBlocked()){
            throw new UserBlockedException("User is blocked");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()
            ));

            user.setFailedLoginAttempts(0);
            userRepository.save(user);

        }
        catch (Exception e) {

            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            if (user.getFailedLoginAttempts() >= max_login_attempts) {
                user.setBlocked(true);

                siemService.logSecurityEvent(
                        "Korisnik: " + user.getUsername() +
                                ", Broj neuspjelih pokušaja: " + user.getFailedLoginAttempts() +
                                ", Vrijeme: " + Instant.now() +
                                " - Nalog blokiran zbog prevelikog broja pokušaja logina."
                );
            }
            userRepository.save(user);
            throw new BadCredentialsException("Invalid username or password", e);
        }


        String code = RandomStringUtils.randomNumeric(6);

        verificationCodeRepository.deleteAllByUsername(user.getUsername());
        verificationCodeRepository.save(new VerificationCode(null,code, user.getUsername(), LocalDateTime.now().plusMinutes(10), 0));
        emailService.sendEmail(user.getEmail(), "Osiguranje AL - verifikacioni kod","Vaš verifikacioni kod je " + code + ".");


        return AuthenticationResponse.builder()
                .twoFactorRequired(true)
                .build();
    }


    public AuthenticationResponse verifyCode(VerificationCodeRequest req) {

        var record = verificationCodeRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("Code not found"));

        var user = userRepository.findByUsername(req.getUsername()).orElseThrow();

        if(!record.getCode().equals(req.getCode())){
            record.setFailedAttempts(record.getFailedAttempts() + 1);
            verificationCodeRepository.save(record);

            if(record.getFailedAttempts() >= max_code_attempts){
                verificationCodeRepository.delete(record);
                user.setBlocked(true);
                userRepository.save(user);
                siemService.logSecurityEvent(
                        "Korisnik: " + user.getUsername() +
                                ", Broj neuspjelih pokušaja: " + record.getFailedAttempts() +
                                ", Vrijeme: " + Instant.now() +
                                " - Nalog blokiran zbog prevelikog broja pokušaja verifikacije koda."
                );

                throw new UserBlockedException("User is blocked");
            }

            throw new BadCredentialsException("Invalid code");
        }


        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            verificationCodeRepository.delete(record);
            throw new BadCredentialsException("Expired code");
        }


        var jwtToken = jwtService.generateToken(user);

        verificationCodeRepository.delete(record);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .twoFactorRequired(false)
                .role(user.getRole().name())
                .build();
    }
}
