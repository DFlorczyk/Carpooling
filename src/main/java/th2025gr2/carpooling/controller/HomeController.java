package th2025gr2.carpooling.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.service.UserService;

import java.security.Principal;

@Controller
public class HomeController {

    private final UserService userService;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model, Principal principal) {
        model.addAttribute("pageTitle", "Home");
        model.addAttribute("view", "home");
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        User user = userService.getCurrentUser(principal);
        String backendCity = (user != null && user.getCity() != null)
                ? user.getCity().getName()
                : null;

        model.addAttribute("backendCity", backendCity);

        return "layout";
    }
}
