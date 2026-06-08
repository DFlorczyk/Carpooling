package th2025gr2.carpooling.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th2025gr2.carpooling.dto.RideDTO;
import th2025gr2.carpooling.model.UserProfile;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;
import th2025gr2.carpooling.service.RideRestService;

import java.util.List;

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
}
