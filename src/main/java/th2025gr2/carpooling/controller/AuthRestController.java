package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.dto.LoginRequest;
import th2025gr2.carpooling.dto.LoginResponse;
import th2025gr2.carpooling.model.UserCredential;
import th2025gr2.carpooling.repository.UserCredentialRepository;
import th2025gr2.carpooling.security.JwtService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<UserCredential> credentialOpt = userCredentialRepository.findByEmail(request.getEmail());
        if (credentialOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Wrong email/password"));
        }
        UserCredential credential = credentialOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), credential.getHashedPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Wrong email/password"));
        }
        if (credential.isBlocked()) {
            return ResponseEntity.status(403).body(Map.of("error", "Blocked"));
        }
        String token = jwtService.generateToken(credential.getEmail(), credential.getId());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
