package th2025gr2.carpooling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.UserRepository;

@Controller
public class ProfileController {
    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = userRepository.findById(1L).orElse(null);
        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("view", "profile");
        model.addAttribute("user", user);
        return "layout";
    }
}