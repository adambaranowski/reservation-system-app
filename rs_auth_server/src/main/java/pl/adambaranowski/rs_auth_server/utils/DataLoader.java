package pl.adambaranowski.rs_auth_server.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.adambaranowski.rs_auth_server.model.User;
import pl.adambaranowski.rs_auth_server.repository.UserRepository;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
            loadSecurityData();
    }

    private void loadSecurityData() {
        userRepository.save(User.builder()
                .email("teacher@gmail.com")
                .password(passwordEncoder.encode("teacher"))
                .build());

        userRepository.save(User.builder()
                .email("student@gmail.com")
                .password(passwordEncoder.encode("student"))
                .build());

        userRepository.save(User.builder()
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("admin"))
                .build());
    }

}
