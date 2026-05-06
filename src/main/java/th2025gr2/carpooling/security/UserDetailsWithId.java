package th2025gr2.carpooling.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import th2025gr2.carpooling.model.UserCredential;

import java.util.Collection;
import java.util.List;

public class UserDetailsWithId implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final boolean blocked;
    private final List<GrantedAuthority> authorities;

    public UserDetailsWithId(UserCredential credential) {
        this.id = credential.getId();
        this.email = credential.getEmail();
        this.password = credential.getHashedPassword();
        this.blocked = credential.isBlocked();
        this.authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + credential.getRole().name())
        );
    }

    public Long getId() { return id; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return !blocked; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}