package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.dto.ApplyRideForm;
import th2025gr2.carpooling.dto.PendingRequestDTO;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.RideService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RideRequestController {

    private final RideService rideService;
    private final UserProfileRepository userProfileRepository;

    @PostMapping("/api/rides/{rideId}/apply")
    public ResponseEntity<?> apply(
            @PathVariable Long rideId,
            @RequestBody ApplyRideForm form,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        if (userDetails == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        UserProfile passenger = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            rideService.applyForRide(rideId, form, passenger);
            return ResponseEntity.ok(Map.of("message", "Application sent"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/rides/{rideId}/requests")
    public ResponseEntity<?> getPendingRequests(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        if (userDetails == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        UserProfile driver = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            List<PendingRequestDTO> requests = rideService.getPendingRequests(rideId, driver);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/api/ride-requests/{requestId}/accept")
    public ResponseEntity<?> accept(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        if (userDetails == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        UserProfile driver = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            rideService.acceptRequest(requestId, driver);
            return ResponseEntity.ok(Map.of("message", "Request accepted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/my-applications")
    public ResponseEntity<?> getMyApplications(
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        if (userDetails == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        UserProfile passenger = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(rideService.getMyApplications(passenger));
    }

    @GetMapping("/api/my-ride-requests")
    public ResponseEntity<?> getMyRideRequests(
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        if (userDetails == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        UserProfile driver = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(rideService.getAllPendingForDriver(driver));
    }

    @PostMapping("/api/ride-requests/{requestId}/reject")
    public ResponseEntity<?> reject(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetailsWithId userDetails
    ) {
        if (userDetails == null) return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));

        UserProfile driver = userProfileRepository.findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        try {
            rideService.rejectRequest(requestId, driver);
            return ResponseEntity.ok(Map.of("message", "Request rejected"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
