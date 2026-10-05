package etfbl.sni.insurance_company.controllers;

import etfbl.sni.insurance_company.dtos.AuthenticationRequest;
import etfbl.sni.insurance_company.dtos.AuthenticationResponse;
import etfbl.sni.insurance_company.dtos.RegisterRequest;
import etfbl.sni.insurance_company.dtos.VerificationCodeRequest;
import etfbl.sni.insurance_company.entities.Role;
import etfbl.sni.insurance_company.exceptions.UserBlockedException;
import etfbl.sni.insurance_company.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){

        try {

            //return ResponseEntity.ok(service.register(request, Role.ADMIN));
             return ResponseEntity.ok(service.register(request, Role.ROLE_USER));
        } catch (ResponseStatusException e) {
            throw new BadCredentialsException("Username or email already exists.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request){
        try {
            return ResponseEntity.ok(service.authenticate(request));
        }
        catch (UserBlockedException e){
            return ResponseEntity.status(HttpStatus.LOCKED).body(null);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<AuthenticationResponse> verifyCode(@RequestBody VerificationCodeRequest request){
        try {
            return ResponseEntity.ok(service.verifyCode(request));
        }
        catch (UserBlockedException e){
            return ResponseEntity.status(HttpStatus.LOCKED).body(null);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
