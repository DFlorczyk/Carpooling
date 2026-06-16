package th2025gr2.carpooling.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.dto.RideDTO;
import th2025gr2.carpooling.dto.RideStopDTO;
import th2025gr2.carpooling.model.Ride;
import th2025gr2.carpooling.model.RideState;
import th2025gr2.carpooling.repository.RideParticipantRepository;
import th2025gr2.carpooling.repository.RideRepository;
import th2025gr2.carpooling.repository.RideStateRepository;
import th2025gr2.carpooling.repository.RideWaypointRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RideRestService {

    private static final Logger log = LoggerFactory.getLogger(RideRestService.class);

    private final RideRepository rideRepository;
    private final RideStateRepository rideStateRepository;
    private final RideWaypointRepository rideWaypointRepository;
    private final RideParticipantRepository rideParticipantRepository;

    public RideRestService(RideRepository rideRepository,
                           RideStateRepository rideStateRepository,
                           RideWaypointRepository rideWaypointRepository,
                           RideParticipantRepository rideParticipantRepository) {
        this.rideRepository = rideRepository;
        this.rideStateRepository = rideStateRepository;
        this.rideWaypointRepository = rideWaypointRepository;
        this.rideParticipantRepository = rideParticipantRepository;
    }

    public List<RideDTO> getRidesDTOByState(String stateName) {
        return rideRepository.findDTOsByStateName(stateName);
    }

    public List<RideDTO> getRidesDrivenBy(Long driverProfileId) {
        return rideRepository.findDTOsByDriverId(driverProfileId);
    }

    public List<RideDTO> getRidesAsPassenger(Long passengerProfileId) {
        return rideRepository.findDTOsByPassengerId(passengerProfileId);
    }

    public List<RideStopDTO> getStopsForRide(Long rideId, Long profileId) {
        if (!rideRepository.existsById(rideId)) {
            throw new RideStateException(HttpStatus.NOT_FOUND, "Przejazd nie istnieje");
        }
        rideParticipantRepository.findByUser_IdAndRide_Id(profileId, rideId)
                .orElseThrow(() -> new RideStateException(
                        HttpStatus.FORBIDDEN, "Nie jesteś uczestnikiem tego przejazdu"));
        return rideWaypointRepository.findStopsByRideId(rideId).stream()
                .map(w -> new RideStopDTO(
                        w.getPassenger().getId(),
                        w.getPassenger().getName() + " " + w.getPassenger().getSurname(),
                        w.getType().name().toLowerCase(),
                        w.getLatitude(), w.getLongitude()))
                .toList();
    }

    public Optional<RideDTO> getActiveRideForDriver(Long driverProfileId) {
        List<RideDTO> actives = rideRepository.findActiveDTOsByDriverId(driverProfileId);
        if (actives.size() > 1) {
            log.warn("Driver profile {} has {} active rides; returning newest by date",
                    driverProfileId, actives.size());
        }
        return actives.stream().findFirst();
    }

    public Optional<RideDTO> getActiveRideForPassenger(Long passengerProfileId) {
        List<RideDTO> actives = rideRepository.findActiveDTOsByPassengerId(passengerProfileId);
        if (actives.size() > 1) {
            log.warn("Passenger profile {} is in {} active rides simultaneously; returning first",
                    passengerProfileId, actives.size());
        }
        return actives.stream().findFirst();
    }

    @Transactional
    public RideDTO startRide(Long rideId, Long driverProfileId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideStateException(
                        HttpStatus.NOT_FOUND, "Przejazd nie istnieje"));

        boolean isDriver = ride.getParticipants() != null && ride.getParticipants().stream()
                .anyMatch(p -> p.getRole() != null
                        && "driver".equalsIgnoreCase(p.getRole().getName())
                        && p.getUser() != null
                        && p.getUser().getId().equals(driverProfileId));
        if (!isDriver) {
            throw new RideStateException(
                    HttpStatus.FORBIDDEN, "Tylko kierowca może rozpocząć ten przejazd");
        }

        String currentState = ride.getState() != null ? ride.getState().getName() : null;
        if (currentState == null || !"not started".equalsIgnoreCase(currentState)) {
            String msg;
            if (currentState == null) {
                msg = "Nieprawidłowy stan przejazdu";
            } else {
                switch (currentState.toLowerCase()) {
                    case "active":    msg = "Przejazd jest już rozpoczęty"; break;
                    case "finished":  msg = "Przejazd jest już zakończony"; break;
                    case "cancelled": msg = "Przejazd jest anulowany"; break;
                    default:          msg = "Nieprawidłowy stan przejazdu: " + currentState;
                }
            }
            throw new RideStateException(HttpStatus.CONFLICT, msg);
        }

        List<RideDTO> alreadyActive = rideRepository.findActiveDTOsByDriverId(driverProfileId);
        if (!alreadyActive.isEmpty()) {
            RideDTO existing = alreadyActive.get(0);
            throw new RideStateException(
                    HttpStatus.CONFLICT,
                    "Masz już aktywny przejazd (id=" + existing.id + ")");
        }

        RideState activeState = rideStateRepository.findByNameIgnoreCase("active")
                .orElseThrow(() -> new RuntimeException(
                        "Brak stanu 'active' w tabeli ride_states"));
        ride.setState(activeState);
        rideRepository.saveAndFlush(ride);

        return rideRepository.findDTOByRideIdAsDriver(rideId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Nie udało się załadować zaktualizowanego przejazdu (id=" + rideId + ")"));
    }

    @Transactional
    public RideDTO finishRide(Long rideId, Long driverProfileId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideStateException(
                        HttpStatus.NOT_FOUND, "Przejazd nie istnieje"));

        boolean isDriver = ride.getParticipants() != null && ride.getParticipants().stream()
                .anyMatch(p -> p.getRole() != null
                        && "driver".equalsIgnoreCase(p.getRole().getName())
                        && p.getUser() != null
                        && p.getUser().getId().equals(driverProfileId));
        if (!isDriver) {
            throw new RideStateException(
                    HttpStatus.FORBIDDEN, "Tylko kierowca może zakończyć ten przejazd");
        }

        String currentState = ride.getState() != null ? ride.getState().getName() : null;
        if (!"active".equalsIgnoreCase(currentState)) {
            String msg;
            if (currentState == null) {
                msg = "Nieprawidłowy stan przejazdu";
            } else {
                switch (currentState.toLowerCase()) {
                    case "not started": msg = "Przejazd jeszcze nie został rozpoczęty"; break;
                    case "finished":    msg = "Przejazd jest już zakończony"; break;
                    case "cancelled":   msg = "Przejazd jest anulowany"; break;
                    default:            msg = "Nieprawidłowy stan przejazdu: " + currentState;
                }
            }
            throw new RideStateException(HttpStatus.CONFLICT, msg);
        }

        RideState finishedState = rideStateRepository.findByNameIgnoreCase("finished")
                .orElseThrow(() -> new RuntimeException(
                        "Brak stanu 'finished' w tabeli ride_states"));
        ride.setState(finishedState);
        rideRepository.saveAndFlush(ride);

        return rideRepository.findDTOByRideIdAsDriver(rideId).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Nie udało się załadować zaktualizowanego przejazdu (id=" + rideId + ")"));
    }
}
