package th2025gr2.carpooling.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.UserRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

@Controller
public class ProfileController {
    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetailsWithId userDetails, Model model
    ) {
        User user = userRepository.findById(userDetails.getId()).orElseThrow();

        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("view", "profile");
        model.addAttribute("user", user);

        return "layout";
    }
}