package th2025gr2.carpooling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.UserRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String editAction(Model model) {

        return "login";
    }
}
