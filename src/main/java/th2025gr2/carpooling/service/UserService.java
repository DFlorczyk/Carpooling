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

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setHashedPassword(passwordEncoder.encode(request.getPassword()));

        user.setSurname("");
        user.set_driver(false);
        user.set_woman(false);
        user.set_blocked(false);
        user.setPhoneNumber(null);

        user.setCity(null);
        user.setCarDetails(null);
        user.setDriverTickets(null);

        return userRepository.save(user);
    }

    public User getCurrentUser(Principal principal) {
        if (principal == null) return null;
        return userRepository.findByEmail(principal.getName()).orElse(null);
    }

    public void updateUserCity(User user, String cityName) {
        if (user == null || cityName == null || cityName.isBlank()) return;

        City city = cityRepository.findByNameIgnoreCase(cityName)
                .orElseGet(() -> {
                    City newCity = new City();
                    newCity.setName(cityName);
                    return cityRepository.save(newCity);
                });

        user.setCity(city);
        userRepository.save(user);
    }
}
