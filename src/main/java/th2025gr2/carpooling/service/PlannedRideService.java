package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import th2025gr2.carpooling.model.*;
import th2025gr2.carpooling.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlannedRideService {

    private final PlannedRideRepository plannedRideRepository;
    private final NotificationRepository notificationRepository;

    public void checkAndNotifyUsers(Ride ride) {
        List<PlannedRide> allTrackedRides = plannedRideRepository.findAll();

        for (PlannedRide track : allTrackedRides) {
            // 1. Sprawdź zakres dat
            boolean dateMatches = !ride.getDate().toLocalDate().isBefore(track.getStartDate()) &&
                    !ride.getDate().toLocalDate().isAfter(track.getEndDate());

            if (dateMatches) {
                // 2. Oblicz odległości
                double startDist = calculateDistance(track.getStartLatitude(), track.getStartLongitude(),
                        ride.getStartLatitude(), ride.getStartLongitude());
                double endDist = calculateDistance(track.getEndLatitude(), track.getEndLongitude(),
                        ride.getEndLatitude(), ride.getEndLongitude());
                // 3. Sprawdź promień ustawiony przez użytkownika
                if (startDist <= track.getRadiusKm() && endDist <= track.getRadiusKm()) {
                    if (!track.getUser().getId().equals(ride.getParticipants().get(0).getUser().getId())) {
                        Notification note = new Notification();
                        note.setUser(track.getUser());
                        note.setMessage("Rides that fit you: Nowy przejazd pasuje do Twojego śledzenia!");
                        note.setRideId(ride.getId());
                        note.setCreatedAt(LocalDateTime.now());
                        notificationRepository.save(note);
                    }
                }
            }
        }
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Promień ziemi
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}