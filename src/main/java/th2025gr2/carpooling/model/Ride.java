//package th2025gr2.carpooling.model;
//
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//@Table(name = "rides")
//public class Ride {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "start_address")
//    private String startAddress;
//
//    @Column(name = "start_latitude", nullable = false)
//    private Double startLatitude;
//
//    @Column(name = "start_longitude", nullable = false)
//    private Double startLongitude;
//
//    @Column(name = "end_address")
//    private String endAddress;
//
//    @Column(name = "end_latitude", nullable = false)
//    private Double endLatitude;
//
//    @Column(name = "end_longitude", nullable = false)
//    private Double endLongitude;
//
//    @Column(name = "date", nullable = false)
//    private LocalDateTime date;
//
//    @Column(name = "cost", nullable = false)
//    private Double cost;
//
//    @Column(name = "is_payed", nullable = false)
//    private Boolean isPayed;
//
//    @Column(name = "available_seats")
//    private Integer availableSeats;
//
//    @Column(name = "distance_km")
//    private Double distanceKm;
//
//    @Column(name = "duration_minutes")
//    private Integer durationMinutes;
//
//    @Column(name = "encoded_polyline", columnDefinition = "TEXT")
//    private String encodedPolyline;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "state_id", nullable = false)
//    private RideState state;
//
//    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<RideRequest> rideRequests;
//
//    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<RideParticipant> participants;
//}
package th2025gr2.carpooling.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rides")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_latitude", nullable = false)
    private Double startLatitude;
    @Column(name = "start_longitude", nullable = false)
    private Double startLongitude;
    @Column(name = "end_latitude", nullable = false)
    private Double endLatitude;
    @Column(name = "end_longitude", nullable = false)
    private Double endLongitude;
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    @Column(name = "cost", nullable = false)
    private Double cost;
    @Column(name = "is_payed", nullable = false)
    private Boolean isPayed;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private RideState state;
    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RideRequest> rideRequests;
    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RideParticipant> participants;

    public void addParticipant(RideParticipant participant) {
        participants.add(participant);
        participant.setRide(this); // IMPORTANT
    }
}