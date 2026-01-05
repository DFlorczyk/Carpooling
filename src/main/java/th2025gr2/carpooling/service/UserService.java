package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import th2025gr2.carpooling.dto.RegisterRequest;
import th2025gr2.carpooling.model.User;
import th2025gr2.carpooling.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono użytkownika: " + email));


        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getHashedPassword(),
                true,  // enabled
                true,  // accountNonExpired
                true,  // credentialsNonExpired
                true,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email jest wymagany");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Hasło jest wymagane");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Użytkownik o takim emailu już istnieje");
        }

        User u = new User();
        u.setEmail(request.getEmail());
        u.setHashedPassword(passwordEncoder.encode(request.getPassword())); // BCrypt
        u.setName(request.getName() == null ? "" : request.getName());
        u.setSurname(request.getSurname() == null ? "" : request.getSurname());
        u.setPhoneNumber(request.getPhoneNumber());


        userRepository.save(u);
    }
}
