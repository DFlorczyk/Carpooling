package th2025gr2.carpooling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import th2025gr2.carpooling.model.UserProfile;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByCredentialId(Long credentialId);
    Optional<UserProfile> findByCredentialEmail(String email);
    List<UserProfile> findByDriverFalse();
    List<UserProfile> findByDriverTrue();
}