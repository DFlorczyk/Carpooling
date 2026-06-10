package th2025gr2.carpooling.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TravelPlannerController {

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @GetMapping("/track-ride")
    public String trackRide(Model model) {
        model.addAttribute("pageTitle", "Track the ride");
        model.addAttribute("view", "TrackRide");
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "layout";
    }
}