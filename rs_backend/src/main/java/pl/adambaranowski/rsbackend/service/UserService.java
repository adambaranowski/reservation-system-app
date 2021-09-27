package pl.adambaranowski.rsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.adambaranowski.rsbackend.exception.ConflictException;
import pl.adambaranowski.rsbackend.exception.NotAllowedException;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.Authority;
import pl.adambaranowski.rsbackend.model.Reservation;
import pl.adambaranowski.rsbackend.model.User;
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

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ReservationRepository reservationRepository;
    private final UserResponseMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoValidator validator;

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

        if(!isAdmin) {
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

        Set<Authority> authorities = requestDto.getAuthorities().stream()
                .map(authority -> authorityRepository.findByRole(authority)
                        .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_AUTHORITY, authority))))
                .collect(Collectors.toSet());

        User user = new User();

        user.setUserNick(requestDto.getUserNick());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.addAuthorities(authorities);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(false);
        user.setJoinDateTime(LocalDateTime.now());

        userRepository.save(user);

        return mapper.mapToDto(user);
    }

    @Transactional
    public UserResponseDto modifyUser(Integer userId, UserRequestDto requestDto) {
        validator.validateUserDto(requestDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_USER, userId)));

        Set<Authority> authorities = requestDto.getAuthorities().stream()
                .map(authority -> authorityRepository.findByRole(authority)
                        .orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_AUTHORITY, authority))))
                .collect(Collectors.toSet());

        user.setUserNick(requestDto.getUserNick());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.getAuthorities().clear();
        user.addAuthorities(authorities);

        return mapper.mapToDto(user);
    }

    public void deleteUserById(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(String.format(NO_SUCH_USER, userId)));

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
}