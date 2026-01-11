package th2025gr2.carpooling.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import th2025gr2.carpooling.repository.RideRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

@Controller
public class RidesHistoryController {

    private final RideRepository rideRepository;

    public RidesHistoryController(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @GetMapping("/ridehistory/{userId}")
    public String rideHistory(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsWithId userDetails,
            Model model
    ) {
        if (!userDetails.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        var rides = rideRepository.findFinishedRidesByUserId(userId);

        model.addAttribute("pageTitle", "RideHistory");
        model.addAttribute("view", "rideshistory");
        model.addAttribute("userId", userId);
        model.addAttribute("rides", rides);

        return "layout";
    }
}



