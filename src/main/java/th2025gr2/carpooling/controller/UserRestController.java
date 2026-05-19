package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.dto.UserProfileResponse;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {

    private final UserProfileRepository userProfileRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Not found"));

        return ResponseEntity.ok(new UserProfileResponse(
                profile.getId(),
                profile.getName(),
                profile.getSurname(),
                userDetails.getUsername(),
                profile.getPhoneNumber(),
                profile.isDriver(),
                profile.isWoman(),
                profile.getCity() != null ? profile.getCity().getName() : null
        ));
    }
}
