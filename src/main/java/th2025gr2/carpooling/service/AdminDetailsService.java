package th2025gr2.carpooling.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import th2025gr2.carpooling.repository.AdminRepository;
import th2025gr2.carpooling.security.AdminDetails;

@Service
@RequiredArgsConstructor
public class AdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return adminRepository.findByLogin(login)
                .map(AdminDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + login));
    }
}