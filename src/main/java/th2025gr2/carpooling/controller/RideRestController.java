package th2025gr2.carpooling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th2025gr2.carpooling.dto.RideDTO;
import th2025gr2.carpooling.dto.RideStopDTO;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.RideRestService;
import th2025gr2.carpooling.service.RideStateException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/rides")
public class RideRestController {

    private final RideRestService rideService;
    private final UserProfileRepository userProfileRepository;

    public RideRestController(RideRestService rideService,
                              UserProfileRepository userProfileRepository) {
        this.rideService = rideService;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("/not-started")
    public List<RideDTO> getNotStartedRides() {
        return rideService.getRidesDTOByState("not started");
    }

    @GetMapping("/my-driven")
    public ResponseEntity<List<RideDTO>> getMyDrivenRides(
            @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ResponseEntity.ok(rideService.getRidesDrivenBy(profile.getId()));
    }

    @GetMapping("/my-passenger")
    public ResponseEntity<List<RideDTO>> getMyPassengerRides(
            @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ResponseEntity.ok(rideService.getRidesAsPassenger(profile.getId()));
    }

    @GetMapping("/{rideId}/stops")
    public ResponseEntity<List<RideStopDTO>> getRideStops(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        return ResponseEntity.ok(rideService.getStopsForRide(rideId, profile.getId()));
    }

    @GetMapping("/active")
    public ResponseEntity<RideDTO> getActiveRide(
            @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        Optional<RideDTO> active = rideService.getActiveRideForDriver(profile.getId());
        return active.<ResponseEntity<RideDTO>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/passenger/active")
    public ResponseEntity<RideDTO> getActiveRideAsPassenger(
            @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        Optional<RideDTO> active = rideService.getActiveRideForPassenger(profile.getId());
        return active.<ResponseEntity<RideDTO>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/{rideId}/start")
    public ResponseEntity<RideDTO> startRide(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        RideDTO dto = rideService.startRide(rideId, profile.getId());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{rideId}/finish")
    public ResponseEntity<RideDTO> finishRide(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        RideDTO dto = rideService.finishRide(rideId, profile.getId());
        return ResponseEntity.ok(dto);
    }

    @ExceptionHandler(RideStateException.class)
    public ResponseEntity<Map<String, String>> handleRideState(RideStateException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of("error", ex.getMessage()));
    }
}
