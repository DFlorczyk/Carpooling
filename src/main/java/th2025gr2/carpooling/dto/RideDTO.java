package th2025gr2.carpooling.dto;

import java.time.LocalDateTime;

public class RideDTO {
    public Long id;
    public Double startLatitude;
    public Double startLongitude;
    public Double endLatitude;
    public Double endLongitude;
    public Double cost;
    public LocalDateTime date;
    public String stateName;
    public String driverName;
    public String carModel;
    public String carColor;
    public Short carSeats;
    public Short carMileage;

    public RideDTO(Long id, Double startLatitude, Double startLongitude,
                   Double endLatitude, Double endLongitude,
                   Double cost, LocalDateTime date,
                   String stateName, String driverName,
                   String carModel, String carColor,
                   Short carSeats, Short carMileage) {
        this.id = id;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.cost = cost;
        this.date = date;
        this.stateName = stateName;
        this.driverName = driverName;
        this.carModel = carModel;
        this.carColor = carColor;
        this.carSeats = carSeats;
        this.carMileage = carMileage;
    }
}