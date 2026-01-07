package th2025gr2.carpooling.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String password;

    private String name;
    private String surname;

    // opcjonalnie, jeśli chcesz rejestrację z tymi danymi:
    private String phoneNumber;
}