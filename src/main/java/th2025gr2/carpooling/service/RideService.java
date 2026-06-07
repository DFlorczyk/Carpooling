package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.dto.CreateRideForm;
import th2025gr2.carpooling.dto.RideDTO;
import th2025gr2.carpooling.dto.RideResponse;
import th2025gr2.carpooling.dto.WaypointDTO;
import th2025gr2.carpooling.model.*;
import th2025gr2.carpooling.repository.RideRepository;
import th2025gr2.carpooling.repository.RideStateRepository;
import th2025gr2.carpooling.repository.RideWaypointRepository;
import th2025gr2.carpooling.repository.RoleRepository;
import th2025gr2.carpooling.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final RideStateRepository rideStateRepository;
    private final RoleRepository roleRepository;
    private final GoogleMapsService googleMapsService;
    private final UserRepository userRepository;
    private final RideWaypointRepository rideWaypointRepository;

    @Transactional
    public Ride createRide(CreateRideForm form, User driver) {

        if (form.getStartAddress() == null || form.getStartAddress().isBlank()) {
            form.setStartAddress(googleMapsService.reverseGeocode(
                    form.getStartLatitude(), form.getStartLongitude()));
        }
        if (form.getEndAddress() == null || form.getEndAddress().isBlank()) {
            form.setEndAddress(googleMapsService.reverseGeocode(
                    form.getEndLatitude(), form.getEndLongitude()));
        }

        if (form.getDistanceKm() == null || form.getDurationMinutes() == null) {
            GoogleMapsService.RouteInfo route = googleMapsService.getDirections(
                    form.getStartLatitude(), form.getStartLongitude(),
                    form.getEndLatitude(), form.getEndLongitude()
            );
            if (route != null) {
                form.setDistanceKm(route.distanceKm());
                form.setDurationMinutes(route.durationMinutes());
                form.setEncodedPolyline(route.encodedPolyline());
            }
        }

        RideState activeState = rideStateRepository.findByNameIgnoreCase("not started")
                .orElseGet(() -> rideStateRepository.findById(2L)
                        .orElseThrow(() -> new RuntimeException(
                                "Brak stanu 'ACTIVE' w tabeli ride_states")));

        Ride ride = new Ride();
        ride.setStartLatitude(form.getStartLatitude());
        ride.setStartLongitude(form.getStartLongitude());
        ride.setEndLatitude(form.getEndLatitude());
        ride.setEndLongitude(form.getEndLongitude());
        ride.setDate(form.getDepartureDate());
        ride.setCost(form.getCost() != null ? form.getCost() : 0.0);
        ride.setIsPayed(false);
        ride.setState(activeState);

        RideParticipant participant = new RideParticipant();
        Optional<RideRole> role = roleRepository.findByNameIgnoreCase("driver");
        participant.setRole(role.get());
        participant.setRide(ride);
        participant.setUser(driver);

        ride.setParticipants(List.of(participant));

        ride = rideRepository.save(ride);

        return ride;
    }

    public List<RideResponse> getActiveRides() {
        List<Ride> rides = rideRepository.findActiveRides();
        return rides.stream().map(this::toResponse).toList();
    }

    public RideResponse getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przejazdu o id: " + id));
        return toResponse(ride);
    }

    public List<WaypointDTO> getWaypointsForRide(Long rideId) {
        return rideWaypointRepository.findByRideIdOrderBySequenceOrderAsc(rideId)
                .stream()
                .map(w -> new WaypointDTO(w.getLatitude(), w.getLongitude(), w.getType().name(), w.getSequenceOrder()))
                .toList();
    }

    public List<RideDTO> getFilteredRides(LocalDateTime dateFrom, LocalDateTime dateTo, Double maxPrice) {
        if (dateFrom == null && dateTo == null && maxPrice == null) {
            return rideRepository.findDTOsByStateName("not started");
        }
        if (dateFrom == null) dateFrom = LocalDateTime.of(2000, 1, 1, 0, 0);
        if (dateTo == null)   dateTo   = LocalDateTime.of(2100, 1, 1, 0, 0);
        if (maxPrice == null) maxPrice = Double.MAX_VALUE;
        return rideRepository.findFilteredRidesWithAllParams(dateFrom, dateTo, maxPrice);
    }

    private RideResponse toResponse(Ride ride) {
        String driverName = "Nieznany";
        Long driverId = null;

        if (ride.getParticipants() != null) {
            for (RideParticipant rp : ride.getParticipants()) {
                if ("driver".equalsIgnoreCase(rp.getRole().getName())) {
                    User driver = rp.getUser();
                    driverName = driver.getName() + " " + driver.getSurname();
                    driverId = driver.getId();
                    break;
                }
            }
        }

        return RideResponse.builder()
                .id(ride.getId())
                .startLatitude(ride.getStartLatitude())
                .startLongitude(ride.getStartLongitude())
                .endLatitude(ride.getEndLatitude())
                .endLongitude(ride.getEndLongitude())
                .departureDate(ride.getDate())
                .cost(ride.getCost())
                .driverName(driverName)
                .driverId(driverId)
                .stateName(ride.getState().getName())
                .build();
    }
}