package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.dto.*;
import th2025gr2.carpooling.enums.WaypointType;
import th2025gr2.carpooling.model.*;
import th2025gr2.carpooling.repository.*;
import th2025gr2.carpooling.service.PlannedRideService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RideService {

    private final PlannedRideService plannedRideService;
    private final RideRepository rideRepository;
    private final RideStateRepository rideStateRepository;
    private final RoleRepository roleRepository;
    private final GoogleMapsService googleMapsService;
    private final RideWaypointRepository rideWaypointRepository;
    private final RideRequestRepository rideRequestRepository;
    private final RideParticipantRepository rideParticipantRepository;

    @Transactional
    public Ride createRide(CreateRideForm form, UserProfile driver) {

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
                                "Brak stanu 'active' w tabeli ride_states")));

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

        plannedRideService.checkAndNotifyUsers(ride);

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

    @Transactional
    public void applyForRide(Long rideId, ApplyRideForm form, UserProfile passenger) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        boolean isDriver = ride.getParticipants().stream()
                .anyMatch(p -> "driver".equalsIgnoreCase(p.getRole().getName())
                            && p.getUser().getId().equals(passenger.getId()));
        if (isDriver) throw new RuntimeException("You cannot apply for your own ride");

        rideRequestRepository.findByUser_IdAndRide_Id(passenger.getId(), rideId)
                .ifPresent(r -> { throw new RuntimeException("You have already applied for this ride"); });

        RideRequest request = new RideRequest();
        request.setRide(ride);
        request.setUser(passenger);
        request.setIsAccepted(null);
        request.setPickupLatitude(form.getPickupLatitude());
        request.setPickupLongitude(form.getPickupLongitude());
        request.setDropoffLatitude(form.getDropoffLatitude());
        request.setDropoffLongitude(form.getDropoffLongitude());
        rideRequestRepository.save(request);
    }

    public List<PendingRequestDTO> getPendingRequests(Long rideId, UserProfile driver) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        boolean isDriver = ride.getParticipants().stream()
                .anyMatch(p -> "driver".equalsIgnoreCase(p.getRole().getName())
                            && p.getUser().getId().equals(driver.getId()));
        if (!isDriver) throw new RuntimeException("Not authorized");

        return rideRequestRepository.findByRide_IdAndIsAcceptedIsNull(rideId).stream()
                .map(r -> new PendingRequestDTO(
                        r.getId(), rideId,
                        r.getUser().getId(),
                        r.getUser().getName() + " " + r.getUser().getSurname(),
                        r.getPickupLatitude(), r.getPickupLongitude(),
                        r.getDropoffLatitude(), r.getDropoffLongitude(),
                        r.getRide().getStartLatitude(), r.getRide().getStartLongitude(),
                        r.getRide().getEndLatitude(), r.getRide().getEndLongitude()
                )).toList();
    }

    public List<MyApplicationDTO> getMyApplications(UserProfile passenger) {
        return rideRequestRepository.findByUser_IdOrderByIdDesc(passenger.getId()).stream()
                .map(r -> {
                    String status = r.getIsAccepted() == null ? "Pending"
                            : (r.getIsAccepted() ? "Accepted" : "Rejected");
                    return new MyApplicationDTO(
                            r.getId(), r.getRide().getId(), status,
                            r.getIsPaid() != null && r.getIsPaid(),
                            r.getRide().getCost(),
                            r.getPickupLatitude(), r.getPickupLongitude(),
                            r.getDropoffLatitude(), r.getDropoffLongitude(),
                            r.getRide().getStartLatitude(), r.getRide().getStartLongitude(),
                            r.getRide().getEndLatitude(), r.getRide().getEndLongitude()
                    );
                }).toList();
    }

    public List<PendingRequestDTO> getAllPendingForDriver(UserProfile driver) {
        return rideRequestRepository.findAllPendingForDriver(driver.getId()).stream()
                .map(r -> new PendingRequestDTO(
                        r.getId(), r.getRide().getId(),
                        r.getUser().getId(),
                        r.getUser().getName() + " " + r.getUser().getSurname(),
                        r.getPickupLatitude(), r.getPickupLongitude(),
                        r.getDropoffLatitude(), r.getDropoffLongitude(),
                        r.getRide().getStartLatitude(), r.getRide().getStartLongitude(),
                        r.getRide().getEndLatitude(), r.getRide().getEndLongitude()
                )).toList();
    }

    @Transactional
    public void acceptRequest(Long requestId, UserProfile driver) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Ride ride = request.getRide();
        boolean isDriver = ride.getParticipants().stream()
                .anyMatch(p -> "driver".equalsIgnoreCase(p.getRole().getName())
                            && p.getUser().getId().equals(driver.getId()));
        if (!isDriver) throw new RuntimeException("Not authorized");

        request.setIsAccepted(true);
        rideRequestRepository.save(request);

        int maxOrder = rideWaypointRepository.findMaxSequenceOrderByRideId(ride.getId());

        RideWaypoint pickup = new RideWaypoint();
        pickup.setRide(ride);
        //pickup.setPassenger(request.getUser());
        pickup.setLatitude(request.getPickupLatitude());
        pickup.setLongitude(request.getPickupLongitude());
        pickup.setType(WaypointType.PICKUP);
        pickup.setSequenceOrder(maxOrder + 1);
        rideWaypointRepository.save(pickup);

        RideWaypoint dropoff = new RideWaypoint();
        dropoff.setRide(ride);
        //dropoff.setPassenger(request.getUser());
        dropoff.setLatitude(request.getDropoffLatitude());
        dropoff.setLongitude(request.getDropoffLongitude());
        dropoff.setType(WaypointType.DROPOFF);
        dropoff.setSequenceOrder(maxOrder + 2);
        rideWaypointRepository.save(dropoff);

        RideParticipant participant = new RideParticipant();
        participant.setRide(ride);
        participant.setUser(request.getUser());
        participant.setRole(roleRepository.findByNameIgnoreCase("passenger")
                .orElseThrow(() -> new RuntimeException("Passenger role not found")));
        rideParticipantRepository.save(participant);
    }

    @Transactional
    public void rejectRequest(Long requestId, UserProfile driver) {
        RideRequest request = rideRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Ride ride = request.getRide();
        boolean isDriver = ride.getParticipants().stream()
                .anyMatch(p -> "driver".equalsIgnoreCase(p.getRole().getName())
                            && p.getUser().getId().equals(driver.getId()));
        if (!isDriver) throw new RuntimeException("Not authorized");

        request.setIsAccepted(false);
        rideRequestRepository.save(request);
    }

    private RideResponse toResponse(Ride ride) {
        String driverName = "Nieznany";
        Long driverId = null;

        if (ride.getParticipants() != null) {
            for (RideParticipant rp : ride.getParticipants()) {
                if ("driver".equalsIgnoreCase(rp.getRole().getName())) {
                    UserProfile driver = rp.getUser();
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