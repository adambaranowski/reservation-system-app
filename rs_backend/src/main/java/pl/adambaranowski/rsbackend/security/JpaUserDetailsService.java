package pl.adambaranowski.rsbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.adambaranowski.rsbackend.model.Authority;
import pl.adambaranowski.rsbackend.model.User;
import pl.adambaranowski.rsbackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JpaUserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User of email: " + email + " not found"));

        //Last login here. It doesn't have to be successful. Last login means just attempt to log.
        user.setLastLoginDateTime(LocalDateTime.now());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("password doesn't matter here")
                .accountLocked(!user.getAccountNonLocked())
                .authorities(covertToSpringAuthorities(user.getAuthorities()))
                .build();
    }

    private Collection<? extends GrantedAuthority> covertToSpringAuthorities(Set<Authority> authorities) {
        if (authorities != null && authorities.size() > 0) {
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}
