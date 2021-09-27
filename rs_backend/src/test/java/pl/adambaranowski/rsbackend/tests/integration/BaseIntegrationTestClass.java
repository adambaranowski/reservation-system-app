package pl.adambaranowski.rsbackend.tests.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import pl.adambaranowski.rsbackend.model.Authority;
import pl.adambaranowski.rsbackend.model.User;
import pl.adambaranowski.rsbackend.model.UserRole;
import pl.adambaranowski.rsbackend.model.dto.LoginRequestDto;
import pl.adambaranowski.rsbackend.model.dto.LoginResponseDto;
import pl.adambaranowski.rsbackend.repository.AuthorityRepository;
import pl.adambaranowski.rsbackend.repository.ReservationRepository;
import pl.adambaranowski.rsbackend.repository.RoomRepository;
import pl.adambaranowski.rsbackend.repository.UserRepository;
import pl.adambaranowski.rsbackend.service.utils.EquipmentResponseMapper;

import javax.annotation.PostConstruct;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static pl.adambaranowski.rsbackend.security.UrlsConstants.LOGIN_ENDPOINT;

@SpringBootTest(properties = "rs.security.enabled=true")
@AutoConfigureMockMvc
public abstract class BaseIntegrationTestClass {
    protected static final int AUTH_HEADER_NAME = 0;
    protected static final int AUTH_HEADER_VALUE = 1;
    private static final String ADMIN_EMAIL = "testadmin@gmail.com";
    private static final String ADMIN_PASSWORD = "testAdmin";
    private static final String ADMIN_NICK = "testAdmin";
    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected AuthorityRepository authorityRepository;
    @Autowired
    protected ReservationRepository reservationRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    EquipmentResponseMapper equipmentResponseMapper;

    @PostConstruct
    protected void configureTestUsers() {
        clearRepositories();
        createAuthorities();
        createUsers();
    }

    private void clearRepositories() {
        reservationRepository.deleteAll();
        userRepository.deleteAll();
        authorityRepository.deleteAll();
    }

    private void createAuthorities() {
        Authority admin = authorityRepository.save(Authority.builder().role(UserRole.ADMIN.name()).build());
    }

    private void createUsers() {
        Authority adminAuthority = authorityRepository.findByRole(UserRole.ADMIN.name()).get();

        User user = new User();
        user.setUserNick(ADMIN_NICK);
        user.setEmail(ADMIN_EMAIL);
        user.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        user.setAccountNonLocked(true);

        userRepository.save(user);

        User getAttachedUser = userRepository.findByEmail(ADMIN_EMAIL).get();
        getAttachedUser.addAuthorities(Set.of(adminAuthority));
        userRepository.save(getAttachedUser);

    }


    protected String[] getAdminTokenHeader() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail(ADMIN_EMAIL);
        requestDto.setPassword(ADMIN_PASSWORD);

        String body = mvc.perform(
                post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestDto))
        )
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponseDto loginResponseDto = mapper.readValue(body, LoginResponseDto.class);

        String headerValue = "Bearer: " + loginResponseDto.getToken();

        return new String[]{"Authentication", headerValue};

    }
}
