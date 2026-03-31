package th2025gr2.carpooling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminLoginController {

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin-login";
    }
}