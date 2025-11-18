package th2025gr2.carpooling.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class MapController {

    @Value("${google.maps.api.key}")
    private String apiKey;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("mapsApiKey", apiKey);
        return "index";
    }
}