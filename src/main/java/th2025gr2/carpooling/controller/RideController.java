package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.dto.CreateRideForm;
import th2025gr2.carpooling.dto.RideDTO;
import th2025gr2.carpooling.dto.RideResponse;
import th2025gr2.carpooling.dto.WaypointDTO;
import th2025gr2.carpooling.model.Ride;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.UserRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.RideService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final UserRepository userRepository;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @GetMapping("/rides/create")
    public String showCreateRideForm(Model model) {
        model.addAttribute("pageTitle", "Dodaj przejazd");
        model.addAttribute("view", "create-ride");
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        model.addAttribute("rideForm", new CreateRideForm());
        return "layout";
    }

    @GetMapping("/rides")
    public String listRides(Model model) {
        List<RideResponse> rides = rideService.getActiveRides();
        model.addAttribute("pageTitle", "Przejazdy");
        model.addAttribute("view", "rides-list");
        model.addAttribute("rides", rides);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "layout";
    }

    @GetMapping("/rides/{id}")
    public String rideDetails(@PathVariable Long id, Model model) {
        RideResponse ride = rideService.getRideById(id);
        model.addAttribute("pageTitle", "Szczegóły przejazdu");
        model.addAttribute("view", "ride-details");
        model.addAttribute("ride", ride);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "layout";
    }

    @PostMapping("/api/rides")
    @ResponseBody
    public ResponseEntity<?> createRide(
            @RequestBody CreateRideForm form,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Musisz być zalogowany, aby dodać przejazd"));
        }

        User driver = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        try {
            Ride ride = rideService.createRide(form, driver);
            return ResponseEntity.ok(Map.of(
                    "message", "Przejazd został dodany pomyślnie",
                    "rideId", ride.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/r/rides/{id}/waypoints")
    @ResponseBody
    public List<WaypointDTO> getRideWaypoints(@PathVariable Long id) {
        return rideService.getWaypointsForRide(id);
    }

    @GetMapping("/api/r/rides/not-started")
    @ResponseBody
    public List<RideDTO> getNotStartedRides(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @RequestParam(required = false) Double maxPrice
    ) {
        return rideService.getFilteredRides(dateFrom, dateTo, maxPrice);
    }
}