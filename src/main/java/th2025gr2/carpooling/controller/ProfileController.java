package th2025gr2.carpooling.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import th2025gr2.carpooling.model.UserCredential;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserCredentialRepository;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

@Controller
public class ProfileController {

    private final UserProfileRepository profileRepository;
    private final UserCredentialRepository credentialRepository;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    public ProfileController(UserProfileRepository profileRepository,
                             UserCredentialRepository credentialRepository) {
        this.profileRepository = profileRepository;
        this.credentialRepository = credentialRepository;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetailsWithId userDetails, Model model) {
        UserProfile user = profileRepository.findByCredentialId(userDetails.getId()).orElseThrow();

        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("view", "profile");
        model.addAttribute("user", user);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        return "layout";
    }

    @GetMapping("/profile/edit")
    public String editProfile(@AuthenticationPrincipal UserDetailsWithId userDetails, Model model) {
        UserProfile user = profileRepository.findByCredentialId(userDetails.getId()).orElseThrow();

        model.addAttribute("pageTitle", "Edit Profile");
        model.addAttribute("view", "user-details");
        model.addAttribute("user", user);

        return "layout";
    }

    @PostMapping("/profile/edit")
    public String saveProfileChanges(@AuthenticationPrincipal UserDetailsWithId userDetails,
                                     @RequestParam String name,
                                     @RequestParam String surname,
                                     @RequestParam(required = false) String email,
                                     @RequestParam(required = false) String phoneNumber) {

        UserProfile profile = profileRepository.findByCredentialId(userDetails.getId()).orElseThrow();
        profile.setName(name);
        profile.setSurname(surname);
        profile.setPhoneNumber(phoneNumber);
        profileRepository.save(profile);

        if (email != null && !email.isBlank()) {
            UserCredential credential = credentialRepository.findById(userDetails.getId()).orElseThrow();
            credential.setEmail(email);
            credentialRepository.save(credential);
        }

        return "redirect:/profile";
    }
}
