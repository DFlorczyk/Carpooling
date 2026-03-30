package th2025gr2.carpooling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import th2025gr2.carpooling.model.Ride;
import th2025gr2.carpooling.service.RideService;
import th2025gr2.carpooling.dto.RideDTO;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @GetMapping("/not-started")
    public List<RideDTO> getNotStartedRides() {
        return rideService.getRidesDTOByState("not started");
    }
}