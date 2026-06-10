package th2025gr2.carpooling.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PlannedRideDTO {
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double radiusKm;
    private String startAddress;
    private String endAddress;
}