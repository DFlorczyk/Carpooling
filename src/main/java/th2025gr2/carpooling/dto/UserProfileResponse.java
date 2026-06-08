package th2025gr2.carpooling.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private boolean driver;
    private boolean woman;
    private String city;
}
