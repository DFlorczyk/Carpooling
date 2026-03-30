package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.dto.CreateRideForm;
import th2025gr2.carpooling.dto.RideResponse;
import th2025gr2.carpooling.model.*;
import th2025gr2.carpooling.repository.RideRepository;
import th2025gr2.carpooling.repository.RideStateRepository;
import th2025gr2.carpooling.repository.RoleRepository;
import th2025gr2.carpooling.repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * Serwis odpowiedzialny za logikę biznesową przejazdów.
 * - Tworzenie nowego przejazdu (z opcjonalnym obliczeniem trasy przez Google Maps)
 * - Pobieranie listy aktywnych przejazdów
 * - Pobieranie szczegółów przejazdu
 */
@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final RideStateRepository rideStateRepository;
    private final RoleRepository roleRepository;
    private final GoogleMapsService googleMapsService;
    private final UserRepository userRepository;

    // ── Tworzenie przejazdu ─────────────────────────────────────────────

    /**
     * Tworzy nowy przejazd na podstawie danych z formularza.
     * Jeśli frontend nie przesłał danych trasy (dystans, czas),
     * serwis sam odpytuje Google Directions API.
     *
     * Kierowca jest automatycznie dodawany jako uczestnik przejazdu z rolą "driver".
     */
    @Transactional
    public Ride createRide(CreateRideForm form, User driver) {

        // Uzupełnienie adresów z reverse-geocodingu, jeśli frontend ich nie podał
        if (form.getStartAddress() == null || form.getStartAddress().isBlank()) {
            form.setStartAddress(googleMapsService.reverseGeocode(
                    form.getStartLatitude(), form.getStartLongitude()));
        }
        if (form.getEndAddress() == null || form.getEndAddress().isBlank()) {
            form.setEndAddress(googleMapsService.reverseGeocode(
                    form.getEndLatitude(), form.getEndLongitude()));
        }

        // Obliczenie trasy, jeśli brak danych z frontu
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

        // Stan "ACTIVE" (id = 2) – przejazd oczekuje na pasażerów
        RideState activeState = rideStateRepository.findByNameIgnoreCase("ACTIVE")
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
        //ride.setAvailableSeats(form.getAvailableSeats() != null ? form.getAvailableSeats() : 3);
        //ride.setDistanceKm(form.getDistanceKm());
        //ride.setDurationMinutes(form.getDurationMinutes());
        //ride.setEncodedPolyline(form.getEncodedPolyline());
        ride.setState(activeState);

        RideParticipant participant = new RideParticipant();
        Optional<Role> role = roleRepository.findByNameIgnoreCase("RIDE-DRIVER");
        participant.setRole(role.get());
        participant.setRide(ride);
        participant.setUser(driver);

        ride.setParticipants(List.of(participant));
        //ride.addParticipant(participant);


        ride = rideRepository.save(ride);

        //ride = rideRepository.save(ride);

        return ride;
    }

    // ── Pobieranie aktywnych przejazdów ─────────────────────────────────

    /**
     * Zwraca listę aktywnych przejazdów (stan ACTIVE), zamapowanych na DTO.
     */
    public List<RideResponse> getActiveRides() {
        List<Ride> rides = rideRepository.findActiveRides();
        return rides.stream().map(this::toResponse).toList();
    }

    // ── Szczegóły przejazdu ─────────────────────────────────────────────

    /**
     * Pobiera przejazd po ID i zwraca DTO.
     */
    public RideResponse getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono przejazdu o id: " + id));
        return toResponse(ride);
    }

    // ── Mapowanie na DTO ────────────────────────────────────────────────

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
                //.startAddress(ride.getStartAddress())
                .startLatitude(ride.getStartLatitude())
                .startLongitude(ride.getStartLongitude())
                //.endAddress(ride.getEndAddress())
                .endLatitude(ride.getEndLatitude())
                .endLongitude(ride.getEndLongitude())
                .departureDate(ride.getDate())
                .cost(ride.getCost())
                //.availableSeats(ride.getAvailableSeats())
                //.distanceKm(ride.getDistanceKm())
                //.durationMinutes(ride.getDurationMinutes())
                .driverName(driverName)
                .driverId(driverId)
                .stateName(ride.getState().getName())
                .build();
    }
}
