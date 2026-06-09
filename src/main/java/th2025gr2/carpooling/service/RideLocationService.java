package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.dto.ParticipantLocationDTO;
import th2025gr2.carpooling.model.ParticipantLocation;
import th2025gr2.carpooling.model.RideParticipant;
import th2025gr2.carpooling.repository.ParticipantLocationRepository;
import th2025gr2.carpooling.repository.RideParticipantRepository;
import th2025gr2.carpooling.repository.RideRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideLocationService {

    private final RideRepository rideRepository;
    private final RideParticipantRepository rideParticipantRepository;
    private final ParticipantLocationRepository locationRepository;

    @Transactional
    public void updateLocation(Long rideId, Long profileId, double latitude, double longitude) {
        var ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideStateException(HttpStatus.NOT_FOUND, "Przejazd nie istnieje"));

        String state = ride.getState() != null ? ride.getState().getName() : null;
        if (!"active".equalsIgnoreCase(state)) {
            throw new RideStateException(HttpStatus.CONFLICT, "Przejazd nie jest aktywny");
        }

        RideParticipant participant = rideParticipantRepository
                .findByUser_IdAndRide_Id(profileId, rideId)
                .orElseThrow(() -> new RideStateException(
                        HttpStatus.FORBIDDEN, "Nie jesteś uczestnikiem tego przejazdu"));

        ParticipantLocation loc = locationRepository
                .findByRide_IdAndProfile_Id(rideId, profileId)
                .orElseGet(() -> {
                    ParticipantLocation newLoc = new ParticipantLocation();
                    newLoc.setRide(ride);
                    newLoc.setProfile(participant.getUser());
                    return newLoc;
                });

        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        loc.setUpdatedAt(LocalDateTime.now());
        locationRepository.save(loc);
    }

    public List<ParticipantLocationDTO> getLocations(Long rideId, Long profileId) {
        if (!rideRepository.existsById(rideId)) {
            throw new RideStateException(HttpStatus.NOT_FOUND, "Przejazd nie istnieje");
        }

        rideParticipantRepository.findByUser_IdAndRide_Id(profileId, rideId)
                .orElseThrow(() -> new RideStateException(
                        HttpStatus.FORBIDDEN, "Nie jesteś uczestnikiem tego przejazdu"));

        return locationRepository.findLocationDTOsByRideId(rideId);
    }
}
