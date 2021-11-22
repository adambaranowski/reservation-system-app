package pl.adambaranowski.rs_auth_server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import pl.adambaranowski.rs_auth_server.exception.TooMuchAttemptsException;
import pl.adambaranowski.rs_auth_server.exception.WrongLoginOrPasswordException;
import pl.adambaranowski.rs_auth_server.model.User;
import pl.adambaranowski.rs_auth_server.model.dto.LoginRequestDto;
import pl.adambaranowski.rs_auth_server.model.dto.LoginResponseDto;
import pl.adambaranowski.rs_auth_server.repository.UserRepository;
import pl.adambaranowski.rs_auth_server.service.bruteforce.LoginAttemptService;

import javax.servlet.http.HttpServletRequest;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;

    @Autowired
    HttpServletRequest request;

    public LoginResponseDto login(LoginRequestDto loginDto) {
        try {
            String ip = getClientIP();
            if (loginAttemptService.isMaxAttemptsExceeded(ip)) {
                throw new TooMuchAttemptsException("You've tried to log too much times. Try again after few minutes");
            }

            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

            User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new NoSuchElementException("User not found"));
            String generatedJwtToken = jwtService.generateTokenForUser(user);

            LoginResponseDto response = new LoginResponseDto();
            response.setToken(generatedJwtToken);
            response.setEmail(user.getEmail());

            return response;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            throw new WrongLoginOrPasswordException("Your password or login is incorrect or your account is locked!");
        }
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}