package th2025gr2.carpooling.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import th2025gr2.carpooling.dto.PlannedRideDTO;
import th2025gr2.carpooling.model.PlannedRide;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.repository.PlannedRideRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import java.util.List;

@RestController
@RequestMapping("/api/planned-rides")
@RequiredArgsConstructor
public class PlannedRideReservationController {

    private final PlannedRideRepository plannedRideRepository;
    private final UserProfileRepository userProfileRepository;

    @PostMapping // TO SŁUŻY DO ROZPOCZĘCIA TRACKOWANIA
    public ResponseEntity<?> createTrack(@RequestBody PlannedRideDTO dto, @AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile user = userProfileRepository.findByCredentialId(userDetails.getId()).orElseThrow();

        PlannedRide plan = new PlannedRide();
        plan.setUser(user);
        plan.setStartLatitude(dto.getStartLatitude());
        plan.setStartLongitude(dto.getStartLongitude());
        plan.setEndLatitude(dto.getEndLatitude());
        plan.setEndLongitude(dto.getEndLongitude());
        plan.setStartAddress(dto.getStartAddress()); // Zapis adresu
        plan.setEndAddress(dto.getEndAddress());     // Zapis adresu
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());
        plan.setRadiusKm(dto.getRadiusKm());

        plannedRideRepository.save(plan);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my-tracks")
    public List<PlannedRide> getMyTracks(@AuthenticationPrincipal UserDetailsWithId userDetails) {
        UserProfile user = userProfileRepository.findByCredentialId(userDetails.getId()).orElseThrow();
        return plannedRideRepository.findByUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable Long id, @AuthenticationPrincipal UserDetailsWithId userDetails) {
        PlannedRide plan = plannedRideRepository.findById(id).orElseThrow();
        if (!plan.getUser().getCredential().getId().equals(userDetails.getId())) {
            return ResponseEntity.status(403).build();
        }
        plannedRideRepository.delete(plan);
        return ResponseEntity.ok().build();
    }
}