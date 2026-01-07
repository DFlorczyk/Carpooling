package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.model.RegisterRequest;
import th2025gr2.carpooling.model.City;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.CityRepository;
import th2025gr2.carpooling.repository.UserRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CityRepository cityRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));
        return new UserDetailsWithId(u);
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email jest wymagany");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Hasło jest wymagane");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Użytkownik o takim emailu już istnieje");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName() == null ? "" : request.getName());
        user.setSurname(request.getSurname() == null ? "" : request.getSurname());
        user.setPhoneNumber(request.getPhoneNumber());
        user.set_driver(false);
        user.set_woman(false);
        user.set_blocked(false);
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
