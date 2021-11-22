package pl.adambaranowski.rsbackend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.adambaranowski.rsbackend.model.User;
import pl.adambaranowski.rsbackend.model.dto.LoginRequestDto;
import pl.adambaranowski.rsbackend.model.dto.LoginResponseDto;
import pl.adambaranowski.rsbackend.model.dto.UserResponseDto;
import pl.adambaranowski.rsbackend.repository.UserRepository;
import pl.adambaranowski.rsbackend.security.bruteforce.LoginAttemptService;
import pl.adambaranowski.rsbackend.security.jwt.JwtService;
import pl.adambaranowski.rsbackend.service.utils.UserResponseMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;
    private final UserResponseMapper mapper;


    public UserResponseDto getUserInfoFromToken() {
        String loggedEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        User user = userRepository.findByEmail(loggedEmail).orElseThrow(() -> new NoSuchElementException("User not found"));

        return mapper.mapToDto(user);
    }

}
