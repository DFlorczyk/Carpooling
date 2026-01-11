package th2025gr2.carpooling.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import th2025gr2.carpooling.dto.DateEventForm;

@Controller
public class TravelPlannerController {

    @GetMapping("/travelPlanner")
    public String TravelPlanner(Model model) {
        model.addAttribute("pageTitle", "Plan");
        model.addAttribute("ev", new DateEventForm());
        model.addAttribute("view", "TravelPlanner");
        return "layout";
    }

    @PostMapping("/travelPlanner")
    public String submitForm(@ModelAttribute DateEventForm eventForm, Model model) {
        model.addAttribute("ev", eventForm);
        model.addAttribute("view", "TravelPlanner");
        System.out.println("Wybrana data i czas: " + eventForm.getDateStartTime() + " " + eventForm.getDateStartEnd());
        return "layout";
    }
}