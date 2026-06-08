package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UserProfileRepository userProfileRepository;

    @ModelAttribute("userIsDriver")
    public boolean userIsDriver(@AuthenticationPrincipal UserDetailsWithId userDetails) {
        if (userDetails == null) return false;

        return userProfileRepository.findByCredentialId(userDetails.getId())
                .map(UserProfile::isDriver)
                .orElse(false);
    }
}
