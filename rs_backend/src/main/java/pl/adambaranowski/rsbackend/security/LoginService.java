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

    @Autowired
    HttpServletRequest request;

    public LoginResponseDto login(LoginRequestDto loginDto) {
//        try {
//            String ip = getClientIP();
//            if (loginAttemptService.isMaxAttemptsExceeded(ip)) {
//                throw new TooMuchAttemptsException("You've tried to log too much times. Try again after few minutes");
//            }
//
//            Authentication authentication = authenticationManager
//                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
//
//            User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(() -> new NoSuchElementException("User not found"));
//            String generatedJwtToken = jwtService.generateTokenForUser(user);
//
//            LoginResponseDto response = new LoginResponseDto();
//            response.setToken(generatedJwtToken);
//            response.setUserId(user.getId());
//            response.setEmail(user.getEmail());
//            response.setUserNick(user.getUserNick());
//            response.setAuthorities(user.getAuthorities()
//                    .stream()
//                    .map(authority -> authority.getRole())
//                    .collect(Collectors.toList()));
//
//            return response;
//        } catch (AuthenticationException e) {
//            throw new WrongLoginOrPasswordException("Your password or login is incorrect or your account is locked!");
//        }
        return null;
    }

    public UserResponseDto getUserInfoFromToken() {
        String loggedEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        User user = userRepository.findByEmail(loggedEmail).orElseThrow(() -> new NoSuchElementException("User not found"));

        return mapper.mapToDto(user);
    }

    private String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
