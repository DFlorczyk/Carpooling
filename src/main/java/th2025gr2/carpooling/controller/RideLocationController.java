package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.dto.LocationUpdateRequest;
import th2025gr2.carpooling.dto.ParticipantLocationDTO;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.RideLocationService;
import th2025gr2.carpooling.service.RideStateException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideLocationController {

    private final RideLocationService rideLocationService;
    private final UserProfileRepository userProfileRepository;

    @PostMapping("/{rideId}/location")
    public ResponseEntity<Void> updateLocation(
            @PathVariable Long rideId,
            @RequestBody LocationUpdateRequest body,
            @AuthenticationPrincipal UserDetailsWithId userDetails) {

        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        rideLocationService.updateLocation(rideId, profile.getId(),
                body.getLatitude(), body.getLongitude());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{rideId}/locations")
    public ResponseEntity<List<ParticipantLocationDTO>> getLocations(
            @PathVariable Long rideId,
            @AuthenticationPrincipal UserDetailsWithId userDetails) {

        UserProfile profile = userProfileRepository
                .findByCredentialId(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<ParticipantLocationDTO> locations =
                rideLocationService.getLocations(rideId, profile.getId());

        return ResponseEntity.ok(locations);
    }

    @ExceptionHandler(RideStateException.class)
    public ResponseEntity<Map<String, String>> handleRideState(RideStateException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(Map.of("error", ex.getMessage()));
    }
}
