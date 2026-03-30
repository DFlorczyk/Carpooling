package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.dto.CreateRideForm;
import th2025gr2.carpooling.dto.RideResponse;
import th2025gr2.carpooling.model.Ride;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.UserRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.RideService;

import java.util.List;
import java.util.Map;

/**
 * Kontroler do zarządzania przejazdami.
 *
 * Endpointy widoków (Thymeleaf):
 *   GET  /rides/create       → formularz dodawania przejazdu z mapą Google Maps
 *   GET  /rides               → lista aktywnych przejazdów
 *   GET  /rides/{id}          → szczegóły przejazdu z mapą trasy
 *
 * Endpointy REST API:
 *   POST /api/rides           → tworzenie przejazdu (JSON)
 *   GET  /api/rides/active    → lista aktywnych przejazdów (JSON)
 */
@Controller
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;
    private final UserRepository userRepository;

    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    // ── Widoki Thymeleaf ────────────────────────────────────────────────

    /**
     * Formularz tworzenia przejazdu z interaktywną mapą Google Maps.
     */
    @GetMapping("/rides/create")
    public String showCreateRideForm(Model model) {
        model.addAttribute("pageTitle", "Dodaj przejazd");
        model.addAttribute("view", "create-ride");
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        model.addAttribute("rideForm", new CreateRideForm());
        return "layout";
    }

    /**
     * Lista aktywnych przejazdów.
     */
    @GetMapping("/rides")
    public String listRides(Model model) {
        List<RideResponse> rides = rideService.getActiveRides();
        model.addAttribute("pageTitle", "Przejazdy");
        model.addAttribute("view", "rides-list");
        model.addAttribute("rides", rides);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "layout";
    }

    /**
     * Szczegóły przejazdu z wyświetloną trasą na mapie.
     */
    @GetMapping("/rides/{id}")
    public String rideDetails(@PathVariable Long id, Model model) {
        RideResponse ride = rideService.getRideById(id);
        model.addAttribute("pageTitle", "Szczegóły przejazdu");
        model.addAttribute("view", "ride-details");
        model.addAttribute("ride", ride);
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "layout";
    }

    // ── REST API ────────────────────────────────────────────────────────

    /**
     * Tworzy nowy przejazd. Wymaga autentykacji.
     * Współrzędne i adresy przychodzą z frontendu (Google Maps Autocomplete + Places).
     */
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
//
//    /**
//     * Zwraca listę aktywnych przejazdów w formacie JSON.
//     */
//    @GetMapping("/api/rides/active")
//    @ResponseBody
//    public ResponseEntity<List<RideResponse>> getActiveRides() {
//        return ResponseEntity.ok(rideService.getActiveRides());
//    }
}
