package th2025gr2.carpooling.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyRideForm {
    private Double pickupLatitude;
    private Double pickupLongitude;
    private Double dropoffLatitude;
    private Double dropoffLongitude;
}
