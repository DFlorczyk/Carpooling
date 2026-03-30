package th2025gr2.carpooling.service;

import org.springframework.stereotype.Service;
import th2025gr2.carpooling.model.Ride;
import th2025gr2.carpooling.repository.RideRepository;
import th2025gr2.carpooling.dto.RideDTO;

import java.util.List;

@Service
public class RideRestService {

    private final RideRepository rideRepository;

    public RideRestService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    public List<RideDTO> getRidesDTOByState(String stateName) {
        return rideRepository.findDTOsByStateName(stateName);
    }
}