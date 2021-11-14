package pl.adambaranowski.rs_auth_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.adambaranowski.rs_auth_server.model.User;
import pl.adambaranowski.rs_auth_server.model.dto.RegisterRequestDto;
import pl.adambaranowski.rs_auth_server.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_EXISTS = "User of given email exists!";
    private static final String NO_SUCH_USER = "User of given email does NOT exist!";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void addNewUser(RegisterRequestDto dto){
        if(userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException(USER_EXISTS);
        }

        userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build());
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NoSuchElementException(NO_SUCH_USER));
        userRepository.delete(user);
    }
}
