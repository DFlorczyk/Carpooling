package th2025gr2.carpooling.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateRideForm {

    private String startAddress;
    private Double startLatitude;
    private Double startLongitude;

    private String endAddress;
    private Double endLatitude;
    private Double endLongitude;

    private LocalDateTime departureDate;

    private Double cost;

    private Integer availableSeats;

    // Google Maps route info
    private String encodedPolyline;
    private Double distanceKm;
    private Integer durationMinutes;
}
