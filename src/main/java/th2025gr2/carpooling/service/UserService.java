package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.enums.Role;
import th2025gr2.carpooling.model.*;
import th2025gr2.carpooling.repository.CityRepository;
import th2025gr2.carpooling.repository.UserCredentialRepository;
import th2025gr2.carpooling.repository.UserProfileRepository;
import th2025gr2.carpooling.security.UserDetailsWithId;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserCredentialRepository credentialRepository;
    private final UserProfileRepository profileRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return credentialRepository.findByEmail(email)
                .map(UserDetailsWithId::new)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));
    }

    @Transactional
    public UserCredential register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new IllegalArgumentException("Email jest wymagany");
        if (request.getPassword() == null || request.getPassword().isBlank())
            throw new IllegalArgumentException("Hasło jest wymagane");
        if (credentialRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Użytkownik o takim emailu już istnieje");

        UserCredential credential = new UserCredential();
        credential.setEmail(request.getEmail());
        credential.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        credential.setBlocked(false);
        credential.setRole(Role.USER);
        credentialRepository.save(credential);

        UserProfile profile = new UserProfile();
        profile.setCredential(credential);
        profile.setName(request.getName() == null ? "" : request.getName());
        profile.setSurname(request.getSurname() == null ? "" : request.getSurname());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setDriver(false);
        profile.setWoman(false);
        profileRepository.save(profile);

        return credential;
    }

    public UserCredential getCurrentCredential(Principal principal) {
        if (principal == null) return null;
        return credentialRepository.findByEmail(principal.getName()).orElse(null);
    }

    public UserProfile getCurrentProfile(Principal principal) {
        if (principal == null) return null;
        return credentialRepository.findByEmail(principal.getName())
                .map(UserCredential::getProfile)
                .orElse(null);
    }

    @Transactional
    public void updateCity(UserProfile profile, String cityName) {
        if (profile == null || cityName == null || cityName.isBlank()) return;

        City city = cityRepository.findByNameIgnoreCase(cityName)
                .orElseGet(() -> {
                    City newCity = new City();
                    newCity.setName(cityName);
                    return cityRepository.save(newCity);
                });

        profile.setCity(city);
        profileRepository.save(profile);
    }
}