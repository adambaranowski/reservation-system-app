package pl.adambaranowski.rsbackend.tests.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
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
import pl.adambaranowski.rsbackend.service.utils.RoomResponseMapper;
import pl.adambaranowski.rsbackend.service.utils.UserResponseMapper;

import javax.annotation.PostConstruct;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static pl.adambaranowski.rsbackend.security.UrlsConstants.LOGIN_ENDPOINT;

@SpringBootTest(properties = "rs.security.enabled=true")
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTestClass {
    protected static final int AUTH_HEADER_NAME = 0;
    protected static final int AUTH_HEADER_VALUE = 1;
    protected static final String ADMIN_EMAIL = "testadmin@gmail.com";
    private static final String ADMIN_PASSWORD = "testAdmin";
    private static final String ADMIN_NICK = "testAdmin";
    protected static final String STUDENT_EMAIL = "student@gmail.com";
    private static final String STUDENT_PASSWORD = "student";
    private static final String STUDENT_NICK = "student";
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
    protected RoomRepository roomRepository;

    @Autowired
    EquipmentResponseMapper equipmentResponseMapper;

    @Autowired
    RoomResponseMapper roomResponseMapper;

    @Autowired
    UserResponseMapper userResponseMapper;


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
        Authority student = authorityRepository.save(Authority.builder().role(UserRole.STUDENT.name()).build());
    }

    private void createUsers() {
        Authority adminAuthority = authorityRepository.findByRole(UserRole.ADMIN.name()).get();
        Authority studentAuthority = authorityRepository.findByRole(UserRole.STUDENT.name()).get();

        User user = new User();
        user.setUserNick(ADMIN_NICK);
        user.setEmail(ADMIN_EMAIL);
        user.setAccountNonLocked(true);

        userRepository.save(user);

        User getAttachedUser = userRepository.findByEmail(ADMIN_EMAIL).get();
        getAttachedUser.addAuthorities(Set.of(adminAuthority));
        userRepository.save(getAttachedUser);


        User student = new User();
        student.setUserNick(STUDENT_NICK);
        student.setEmail(STUDENT_EMAIL);
        student.setAccountNonLocked(true);

        userRepository.save(student);

        User getAttachedStudent = userRepository.findByEmail(STUDENT_EMAIL).get();
        getAttachedStudent.addAuthorities(Set.of(studentAuthority));
        userRepository.save(getAttachedStudent);

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

    protected String[] getStudentTokenHeader() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail(STUDENT_EMAIL);
        requestDto.setPassword(STUDENT_PASSWORD);

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
