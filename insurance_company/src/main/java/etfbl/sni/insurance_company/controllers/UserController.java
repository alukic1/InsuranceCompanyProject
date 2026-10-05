package etfbl.sni.insurance_company.controllers;


import etfbl.sni.insurance_company.dtos.AuthenticationResponse;
import etfbl.sni.insurance_company.dtos.RegisterRequest;
import etfbl.sni.insurance_company.dtos.ViewUserDto;
import etfbl.sni.insurance_company.entities.Role;
import etfbl.sni.insurance_company.mappers.UserMapper;
import etfbl.sni.insurance_company.repositories.UserRepository;
import etfbl.sni.insurance_company.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationService authenticationService;

    @Value("${siem.max.login.failed.attempts}")
    private int maxAttempts;

    @GetMapping
    public List<ViewUserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViewUserDto> getUserById(@PathVariable Long id) {

        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }

    @PostMapping("/add-admin")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){

        return ResponseEntity.ok(authenticationService.register(request, Role.ROLE_ADMIN));
    }


    @PutMapping("/{id}/toggle-block")
    public ResponseEntity<ViewUserDto> toggleBlock(@PathVariable Long id) {

        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (user.isBlocked() && user.getFailedLoginAttempts() == maxAttempts) {
            user.setFailedLoginAttempts(0);
        }

        user.setBlocked(!user.isBlocked());

        userRepository.save(user);
        return ResponseEntity.ok(userMapper.toUserDto(user));
    }
}
