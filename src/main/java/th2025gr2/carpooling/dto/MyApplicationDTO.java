package th2025gr2.carpooling.dto;

public class MyApplicationDTO {
    public Long id;
    public Long rideId;
    public String status;
    public Boolean isPaid;
    public Double rideCost;
    public Double pickupLatitude;
    public Double pickupLongitude;
    public Double dropoffLatitude;
    public Double dropoffLongitude;
    public Double rideStartLatitude;
    public Double rideStartLongitude;
    public Double rideEndLatitude;
    public Double rideEndLongitude;

    public MyApplicationDTO(Long id, Long rideId, String status, Boolean isPaid, Double rideCost,
                             Double pickupLatitude, Double pickupLongitude,
                             Double dropoffLatitude, Double dropoffLongitude,
                             Double rideStartLatitude, Double rideStartLongitude,
                             Double rideEndLatitude, Double rideEndLongitude) {
        this.id = id;
        this.rideId = rideId;
        this.status = status;
        this.isPaid = isPaid;
        this.rideCost = rideCost;
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
