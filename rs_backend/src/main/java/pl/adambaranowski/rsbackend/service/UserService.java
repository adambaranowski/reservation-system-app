package pl.adambaranowski.rsbackend.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import pl.adambaranowski.rsbackend.exception.ConflictException;
import pl.adambaranowski.rsbackend.exception.ExternalServiceCommunicationException;
import pl.adambaranowski.rsbackend.exception.NotAllowedException;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.Authority;
import pl.adambaranowski.rsbackend.model.Reservation;
import pl.adambaranowski.rsbackend.model.User;
import pl.adambaranowski.rsbackend.model.UserRole;
import pl.adambaranowski.rsbackend.model.dto.UserRequestDto;
import pl.adambaranowski.rsbackend.model.dto.UserResponseDto;
import pl.adambaranowski.rsbackend.repository.AuthorityRepository;
import pl.adambaranowski.rsbackend.repository.ReservationRepository;
import pl.adambaranowski.rsbackend.repository.UserRepository;
import pl.adambaranowski.rsbackend.service.utils.UserResponseMapper;
import pl.adambaranowski.rsbackend.validator.UserDtoValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String NO_SUCH_AUTHORITY = "Role %s does not exist";
    private static final String NO_SUCH_USER = "User: %s does not exist";
    private static final String USER_EXISTS = "User of given nick or Email exists!";
    private static final String CREDENTIALS_SAVING_ERROR_MESSAGE = "Error occured during communication with Auth Server";

    private static final String REGISTER_URL = "http://localhost:8081/register";

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ReservationRepository reservationRepository;
    private final UserResponseMapper mapper;
    private final UserDtoValidator validator;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto getUserById(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_USER, userId)));

        String loggedInUserEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        User loggedIn = userRepository.findByEmail(loggedInUserEmail).orElseThrow(() -> new NoSuchElementException("Logged in user not found"));

        boolean isAdmin = loggedIn.getAuthorities()
                .stream()
                .map(authority -> authority.getRole())
                .anyMatch(role -> role.equals("ADMIN"));

        if (!isAdmin) {
            if (!user.getEmail().equals(loggedInUserEmail)) {
                throw new NotAllowedException("You're trying to get information about not your account!");
            }
        }
        return mapper.mapToDto(user);
    }

    public UserResponseDto addNewUser(UserRequestDto requestDto) {
        validator.validateUserDto(requestDto);

        if (userRepository.existsByUserNickOrEmail(requestDto.getUserNick(), requestDto.getEmail())) {
            throw new ConflictException(USER_EXISTS);
        }

        // SAVE CREDENTIALS IN AUTH SERVER
        HttpEntity<LoginPasswordDto> request =
                new HttpEntity<>(new LoginPasswordDto(requestDto.getEmail(), requestDto.getPassword()));

        ResponseEntity<Object> response = null;
        try {
            response = restTemplate.postForEntity(REGISTER_URL, request, Object.class);
        } catch (Exception e) {
            throw new ExternalServiceCommunicationException(CREDENTIALS_SAVING_ERROR_MESSAGE);
        }

        if(response == null || !(response.getStatusCode().value()==201)){
            throw new ExternalServiceCommunicationException(CREDENTIALS_SAVING_ERROR_MESSAGE);
        }
        //

        User user = new User();

        user.setUserNick(requestDto.getUserNick());
        user.setEmail(requestDto.getEmail());

        //prepare lowest authority for registering
        String studentRole = UserRole.STUDENT.name();
        Authority studentAuthority = authorityRepository.findByRole(studentRole)
                .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_AUTHORITY, studentRole)));

        user.addAuthorities(Set.of(studentAuthority));
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(false);
        user.setJoinDateTime(LocalDateTime.now());

        userRepository.save(user);

        return mapper.mapToDto(user);
    }

    /**
     * This method takes requestDto and updates value if it's not-null and valid.
     * If you don't want to update all values of user just put dto in which other values are null
     */
    @Transactional
    public UserResponseDto modifyUser(Integer userId, UserRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_USER, userId)));

        if (validator.isEmailValid(requestDto)) {
            user.setEmail(requestDto.getEmail());
        }

        if (validator.isNickValid(requestDto)) {
            user.setUserNick(requestDto.getUserNick());
        }

        return mapper.mapToDto(user);
    }

    @Transactional
    public void modifyAuthorities(Integer userId, List<String> authorities) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_USER, userId)));

        String currentlyLoggedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getEmail().equals(currentlyLoggedUserEmail)) {
            throw new WrongDtoException(List.of("You cannot modify your authorities."));
        }

        Set<Authority> authoritySet = authorities.stream()
                .map(authority -> authorityRepository.findByRole(authority)
                        .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_AUTHORITY, authority))))
                .collect(Collectors.toSet());

        user.removeAuthorities();
        user.addAuthorities(authoritySet);
    }

    public void deleteUserById(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_USER, userId)));

        String currentlyLoggedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getEmail().equals(currentlyLoggedUserEmail)) {
            throw new WrongDtoException(List.of("You cannot remove yourself"));
        }

        Set<Authority> authorities = user.getAuthorities();
        authorities.forEach(authority -> authority.getUsers().remove(user));
        List<Reservation> byUser = reservationRepository.findByUser(user);
        reservationRepository.deleteAll(byUser);

        //todo fix removing reservations
        userRepository.delete(user);
    }

    @Transactional
    public void enableAccounts(List<Integer> usersIds) {
        usersIds.stream()
                .map(userRepository::findById)
                .forEach(user -> user.ifPresent(extractedUser -> extractedUser.setAccountNonLocked(true)));
    }

    @Transactional
    public void disableAccounts(List<Integer> usersIds) {
        String currentlyLoggedUserEmail = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        usersIds.stream()
                .map(userRepository::findById)
                .forEach(user -> user.ifPresent(extractedUser -> {
                            if (user.get().getEmail().equals(currentlyLoggedUserEmail)) {
                                throw new WrongDtoException(List.of("You cannot block yourself"));
                            }
                            extractedUser.setAccountNonLocked(false);
                        }
                ));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class LoginPasswordDto {
        private String email;
        private String password;
    }
}