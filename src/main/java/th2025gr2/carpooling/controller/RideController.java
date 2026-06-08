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
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.RideService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final UserProfileRepository userProfileRepository;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    @GetMapping("/rides/create")
    public String showCreateRideForm(@AuthenticationPrincipal UserDetailsWithId userDetails, Model model) {
        UserProfile user = userProfileRepository.findByCredentialId(userDetails.getId()).orElseThrow();
        if (!user.isDriver()) return "redirect:/driver/register";

        model.addAttribute("pageTitle", "Dodaj przejazd");
        model.addAttribute("view", "create-ride");
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        model.addAttribute("rideForm", new CreateRideForm());
        return "layout";
    }

    @GetMapping("/rides/{id}")
    public String rideDetails(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetailsWithId userDetails,
                               Model model) {
        RideResponse ride = rideService.getRideById(id);

        boolean isDriver = false;
        if (userDetails != null && ride.getDriverId() != null) {
            Optional<UserProfile> profile = userProfileRepository.findByCredentialId(userDetails.getId());
            isDriver = profile.map(p -> p.getId().equals(ride.getDriverId())).orElse(false);
        }

        model.addAttribute("pageTitle", "Szczegóły przejazdu");
        model.addAttribute("view", "ride-details");
        model.addAttribute("ride", ride);
        model.addAttribute("isDriver", isDriver);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "layout";
    }

    @GetMapping("/rides/{id}/requests")
    public String rideRequests(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetailsWithId userDetails,
                                Model model) {
        RideResponse ride = rideService.getRideById(id);

        UserProfile driver = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isDriver = ride.getDriverId() != null && ride.getDriverId().equals(driver.getId());
        if (!isDriver) return "redirect:/rides/" + id;

        model.addAttribute("pageTitle", "Zgłoszenia do przejazdu");
        model.addAttribute("view", "ride-requests");
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

        UserProfile driver = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Nie znaleziono użytkownika"));

        if (!driver.isDriver()) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Tylko zarejestrowani kierowcy mogą dodawać przejazdy"));
        }

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
