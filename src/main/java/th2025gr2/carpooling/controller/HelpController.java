package th2025gr2.carpooling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {

    @GetMapping("/help")
    public String help(Model model) {
        model.addAttribute("pageTitle", "Help");
        model.addAttribute("view", "help");
        return "layout";
    }
}