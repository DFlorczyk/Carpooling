package th2025gr2.carpooling.controller;

import org.springframework.stereotype.Service;
import th2025gr2.carpooling.model.Ride;
import th2025gr2.carpooling.repository.RideRepository;
import th2025gr2.carpooling.dto.RideDTO;

import java.util.List;

@Service
public class RideService {

    private final RideRepository rideRepository;

    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<RideDTO> getRidesDTOByState(String stateName) {
        return rideRepository.findDTOsByStateName(stateName);
    }
}