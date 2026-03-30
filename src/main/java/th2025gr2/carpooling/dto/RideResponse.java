package th2025gr2.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RideResponse {

    private Long id;

    private String startAddress;
    private Double startLatitude;
    private Double startLongitude;

    private String endAddress;
    private Double endLatitude;
    private Double endLongitude;

    private LocalDateTime departureDate;

    private Double cost;
    private Integer availableSeats;

    private Double distanceKm;
    private Integer durationMinutes;

    private String driverName;
    private Long driverId;
    private String stateName;
}
