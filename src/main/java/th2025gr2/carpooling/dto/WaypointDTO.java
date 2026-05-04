package th2025gr2.carpooling.dto;

public class WaypointDTO {
    public Double latitude;
    public Double longitude;
    public String type;
    public Integer sequenceOrder;

    public WaypointDTO(Double latitude, Double longitude, String type, Integer sequenceOrder) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.sequenceOrder = sequenceOrder;
    }
}
