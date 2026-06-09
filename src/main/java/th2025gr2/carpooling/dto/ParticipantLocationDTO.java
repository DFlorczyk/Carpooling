package th2025gr2.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantLocationDTO {
    private Long profileId;
    private String name;
    private String role;
    private Double latitude;
    private Double longitude;
}
