package th2025gr2.carpooling.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import th2025gr2.carpooling.model.Admin;

import java.util.Collection;
import java.util.List;

public class AdminDetails implements UserDetails {

    private final Long id;
    private final String login;
    private final String password;
    private final List<GrantedAuthority> authorities;

    public AdminDetails(Admin admin) {
        this.id = admin.getId();
        this.login = admin.getLogin();
        this.password = admin.getHashedPassword();
        this.authorities = List.of(
                new SimpleGrantedAuthority(
                        admin.isSuperAdmin() ? "ROLE_SUPER_ADMIN" : "ROLE_ADMIN"
                )
        );
    }

    public Long getId() { return id; }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return login; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}