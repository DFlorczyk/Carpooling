package th2025gr2.carpooling.dto;

public class PendingRequestDTO {
    public Long id;
    public Long rideId;
    public Long passengerProfileId;
    public String passengerName;
    public Double pickupLatitude;
    public Double pickupLongitude;
    public Double dropoffLatitude;
    public Double dropoffLongitude;
    public Double rideStartLatitude;
    public Double rideStartLongitude;
    public Double rideEndLatitude;
    public Double rideEndLongitude;

    public PendingRequestDTO(Long id, Long rideId, Long passengerProfileId, String passengerName,
                              Double pickupLatitude, Double pickupLongitude,
                              Double dropoffLatitude, Double dropoffLongitude,
                              Double rideStartLatitude, Double rideStartLongitude,
                              Double rideEndLatitude, Double rideEndLongitude) {
        this.id = id;
        this.rideId = rideId;
        this.passengerProfileId = passengerProfileId;
        this.passengerName = passengerName;
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.dropoffLatitude = dropoffLatitude;
        this.dropoffLongitude = dropoffLongitude;
        this.rideStartLatitude = rideStartLatitude;
        this.rideStartLongitude = rideStartLongitude;
        this.rideEndLatitude = rideEndLatitude;
        this.rideEndLongitude = rideEndLongitude;
    }
}
