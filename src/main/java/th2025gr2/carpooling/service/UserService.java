package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import th2025gr2.carpooling.model.RegisterRequest;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Hasła nie są identyczne.");
        }

        User user = new User();

        // wymagane pola
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setHashedPassword(passwordEncoder.encode(request.getPassword()));

        // pola wymagane przez encję, ale nie podawane przy rejestracji
        user.setSurname("");                 // puste nazwisko
        user.set_driver(false);              // domyślnie nie jest kierowcą
        user.set_woman(false);               // jeśli chcesz — można dodać do formularza
        user.set_blocked(false);             // nowy użytkownik nie jest zablokowany
        user.setPhoneNumber(null);           // opcjonalnie później w profilu

        user.setCity(null);
        user.setCarDetails(null);
        user.setDriverTickets(null);

        return userRepository.save(user);
    }
}
