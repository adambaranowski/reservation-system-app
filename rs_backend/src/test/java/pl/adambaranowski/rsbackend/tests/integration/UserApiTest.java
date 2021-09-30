package pl.adambaranowski.rsbackend.tests.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.adambaranowski.rsbackend.model.*;
import pl.adambaranowski.rsbackend.model.dto.*;
import pl.adambaranowski.rsbackend.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.adambaranowski.rsbackend.security.UrlsConstants.*;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;
import static pl.adambaranowski.rsbackend.validator.UserDtoValidator.PASSWORD_INSTRUCTIONS;

public class UserApiTest extends BaseIntegrationTestClass{

    @Autowired
    UserRepository userRepository;

    @Test
    void getAllUsers_returnsUserList() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        String body = mvc.perform(
                get(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserResponseDto> userDtos = Arrays.asList(mapper.readValue(body, UserResponseDto[].class));

        List<UserResponseDto> userDtosInDatabase = userRepository.findAll().stream()
                .map(userResponseMapper::mapToDto)
                .collect(Collectors.toList());

        assertEquals(userDtos, userDtosInDatabase);
    }

    @Test
    void getAllUsers_notAllowed_returns403Status() throws Exception{
        String[] studentTokenHeader = getStudentTokenHeader();

        mvc.perform(
                get(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void getAllUsers_unauthorized_returns403Status() throws Exception{
        mvc.perform(
                get(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void addNewUser_returnsCorrectDto() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        UserRequestDto userRequest = UserRequestDto.builder()
                .userNick(TEST_NICK)
                .authorities(TEST_AUTHORITIES)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .email(TEST_VALID_EMAIL_ADDRESS)
                .build();

        String body = mvc.perform(
                post(USERS_ENDPOINT)
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponseDto expectedResponseDto = UserResponseDto.builder()
                .userNick(TEST_NICK)
                .authorities(TEST_AUTHORITIES)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .build();

        UserResponseDto actualResponseDto = mapper.readValue(body, UserResponseDto.class);

        User userInDatabase = userRepository.findByEmail(TEST_VALID_EMAIL_ADDRESS).get();
        List<String> authoritiesInDatabase = userInDatabase.getAuthorities().stream()
                .map(Authority::getRole)
                .collect(Collectors.toList());

        assertEquals(expectedResponseDto.getUserNick(), actualResponseDto.getUserNick());
        assertEquals(expectedResponseDto.getAuthorities(), actualResponseDto.getAuthorities());
        assertEquals(expectedResponseDto.getEmail(), actualResponseDto.getEmail());

        assertEquals(expectedResponseDto.getUserNick(), userInDatabase.getUserNick());
        assertEquals(expectedResponseDto.getAuthorities(), authoritiesInDatabase);
        assertEquals(expectedResponseDto.getEmail(), userInDatabase.getEmail());
    }

    @Test
    void addNewUser_emptyParams_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        UserRequestDto userRequest = UserRequestDto.builder()
                .userNick(null)
                .authorities(null)
                .password(null)
                .email(null)
                .build();

        String body = mvc.perform(
                post(USERS_ENDPOINT)
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();


        List<String> expectedErrors = List.of(
                PASSWORD_INSTRUCTIONS,
                EMPTY_EMAIL,
                NO_AUTHORITY,
                EMPTY_NICK
        );

        List<String> actualErrors = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrors.toString(), actualErrors.toString());
    }

    @Test
    void addNewUser_wrongParams_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        UserRequestDto userRequest = UserRequestDto.builder()
                .userNick(generateTestString(NICK_MAX_LENGTH + 1))
                .authorities(Collections.emptyList())
                .password(InvalidPasswords.NO_DIGIT.getPassword())
                .email(InvalidEmailAddresses.NO_AT.getEmail())
                .build();

        String body = mvc.perform(
                post(USERS_ENDPOINT)
                        .content(mapper.writeValueAsString(userRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> expectedErrors = List.of(
                PASSWORD_INSTRUCTIONS,
                WRONG_EMAIL_PATTERN,
                NO_AUTHORITY,
                TOO_LONG_NICK
        );

        List<String> actualErrors = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrors.toString(), actualErrors.toString());
    }

    @Test
    void getUserById_returnsCorrectUser() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        String body = mvc.perform(
                get(USERS_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponseDto expectedResponseDto = userResponseMapper.mapToDto(user);

        UserResponseDto actualResponseDto = mapper.readValue(body, UserResponseDto.class);

        User userInDatabase = userRepository.findByEmail(TEST_VALID_EMAIL_ADDRESS).get();
        List<String> authoritiesInDatabase = userInDatabase.getAuthorities().stream()
                .map(Authority::getRole)
                .collect(Collectors.toList());

        assertEquals(expectedResponseDto.getUserNick(), actualResponseDto.getUserNick());
        assertEquals(expectedResponseDto.getAuthorities(), actualResponseDto.getAuthorities());
        assertEquals(expectedResponseDto.getEmail(), actualResponseDto.getEmail());

        assertEquals(expectedResponseDto.getUserNick(), userInDatabase.getUserNick());
        assertEquals(expectedResponseDto.getAuthorities(), authoritiesInDatabase);
        assertEquals(expectedResponseDto.getEmail(), userInDatabase.getEmail());
    }

    @Test
    void getUserById_wrongId_returns404Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();
        int wrongId = id + 1;

        mvc.perform(
                get(USERS_ENDPOINT + "/" + wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    void getUserById_notAllowed_returns403Status() throws Exception{
        String[] studentTokenHeader = getStudentTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        mvc.perform(
                get(USERS_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void getUserById_unauthorized_returns403Status() throws Exception{
        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        mvc.perform(
                get(USERS_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void updateUserById_returnsCorrectUser() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        String updatedNick = TEST_NICK + 123;

        UserRequestDto updateUserRequest = UserRequestDto.builder()
                .userNick(updatedNick)
                .authorities(TEST_AUTHORITIES)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .email(TEST_VALID_EMAIL_ADDRESS)
                .build();

        String body = mvc.perform(
                put(USERS_ENDPOINT + "/" + id)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserResponseDto expectedResponseDto = userResponseMapper.mapToDto(user);
        expectedResponseDto.setUserNick(updatedNick);
        expectedResponseDto.setAuthorities(TEST_AUTHORITIES);

        UserResponseDto actualResponseDto = mapper.readValue(body, UserResponseDto.class);

        User userInDatabase = userRepository.findByEmail(TEST_VALID_EMAIL_ADDRESS).get();
        List<String> authoritiesInDatabase = userInDatabase.getAuthorities().stream()
                .map(Authority::getRole)
                .collect(Collectors.toList());

        assertEquals(expectedResponseDto.getUserNick(), actualResponseDto.getUserNick());
        assertEquals(expectedResponseDto.getAuthorities(), actualResponseDto.getAuthorities());
        assertEquals(expectedResponseDto.getEmail(), actualResponseDto.getEmail());

        assertEquals(expectedResponseDto.getUserNick(), userInDatabase.getUserNick());
        assertEquals(expectedResponseDto.getAuthorities(), authoritiesInDatabase);
        assertEquals(expectedResponseDto.getEmail(), userInDatabase.getEmail());
    }

    @Test
    void updateUserById_wrongId_returns404Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();
        int wrongId = id + 1;

        String updatedNick = TEST_NICK + 123;

        UserRequestDto updateUserRequest = UserRequestDto.builder()
                .userNick(updatedNick)
                .authorities(TEST_AUTHORITIES)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .email(TEST_VALID_EMAIL_ADDRESS)
                .build();

        mvc.perform(
                put(USERS_ENDPOINT + "/" + wrongId)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    void updateUserById_emptyData_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        UserRequestDto updateUserRequest = UserRequestDto.builder()
                .userNick(null)
                .authorities(null)
                .password(null)
                .email(null)
                .build();

        String body = mvc.perform(
                put(USERS_ENDPOINT + "/" + id)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> expectedErrors = List.of(
                PASSWORD_INSTRUCTIONS,
                EMPTY_EMAIL,
                NO_AUTHORITY,
                EMPTY_NICK
        );

        List<String> actualErrors = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrors.toString(), actualErrors.toString());
    }

    @Test
    void updateUserById_wrongData_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        UserRequestDto updateUserRequest = UserRequestDto.builder()
                .userNick(generateTestString(NICK_MAX_LENGTH + 1))
                .authorities(Collections.emptyList())
                .password(InvalidPasswords.NO_DIGIT.getPassword())
                .email(InvalidEmailAddresses.NO_AT.getEmail())
                .build();

        String body = mvc.perform(
                put(USERS_ENDPOINT + "/" + id)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> expectedErrors = List.of(
                PASSWORD_INSTRUCTIONS,
                WRONG_EMAIL_PATTERN,
                NO_AUTHORITY,
                TOO_LONG_NICK
        );

        List<String> actualErrors = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        assertEquals(expectedErrors.toString(), actualErrors.toString());
    }

    @Test
    void updateUserById_notAllowed_returns403Status() throws Exception{
        String[] studentTokenHeader = getStudentTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        String updatedNick = TEST_NICK + 123;

        UserRequestDto updateUserRequest = UserRequestDto.builder()
                .userNick(updatedNick)
                .authorities(TEST_AUTHORITIES)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .email(TEST_VALID_EMAIL_ADDRESS)
                .build();

        mvc.perform(
                put(USERS_ENDPOINT + "/" + id)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        User userInDatabase = userRepository.findById(id).get();

        assertEquals(TEST_NICK, userInDatabase.getUserNick());
    }

    @Test
    void updateUserById_unauthorized_returns403Status() throws Exception{
        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        User attachedUser = userRepository.save(user);

        int id = attachedUser.getId();

        String updatedNick = TEST_NICK + 123;

        UserRequestDto updateUserRequest = UserRequestDto.builder()
                .userNick(updatedNick)
                .authorities(TEST_AUTHORITIES)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .email(TEST_VALID_EMAIL_ADDRESS)
                .build();

        mvc.perform(
                put(USERS_ENDPOINT + "/" + id)
                        .content(mapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();

        User userInDatabase = userRepository.findById(id).get();

        assertEquals(TEST_NICK, userInDatabase.getUserNick());
    }

    @Test
    void deleteUserById_successResponse() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id = userRepository.save(user).getId();

        mvc.perform(
                delete(USERS_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Optional<User> userInDatabase = userRepository.findById(id);
        assertTrue(userInDatabase.isEmpty());
    }

    @Test
    void deleteUserById_notFound_returns404Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id = userRepository.save(user).getId();
        int wrongId = id + 1;

        mvc.perform(
                delete(USERS_ENDPOINT + "/" + wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse();
    }

    @Test
    void deleteUserById_notAllowed_returns403Status() throws Exception{
        String[] studentTokenHeader = getStudentTokenHeader();

        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id = userRepository.save(user).getId();

        mvc.perform(
                delete(USERS_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void deleteUserById_unauthorized_returns403Status() throws Exception{
        User user = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id = userRepository.save(user).getId();

        mvc.perform(
                delete(USERS_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void disableUsers_successfulResponse() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user1 = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id1 = userRepository.save(user1).getId();

        User user2 = User.builder()
                .userNick(TEST_NICK + "test")
                .email(TEST_VALID_EMAIL_ADDRESS + "m")
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id2 = userRepository.save(user2).getId();

        List<Integer> requestedUsers = List.of(id1, id2);

        mvc.perform(
                post(USERS_ENDPOINT + "/" + "disable")
                        .content(mapper.writeValueAsString(requestedUsers))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        User user1inDatabase = userRepository.findById(id1).get();
        User user2inDatabase = userRepository.findById(id2).get();

        assertFalse(user1inDatabase.getAccountNonLocked());
        assertFalse(user2inDatabase.getAccountNonLocked());
    }

    @Test
    void disableUsers_notAllowed_returns403Status() throws Exception{
        String[] studentTokenHeader = getStudentTokenHeader();

        User user1 = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id1 = userRepository.save(user1).getId();

        User user2 = User.builder()
                .userNick(TEST_NICK + "test")
                .email(TEST_VALID_EMAIL_ADDRESS + "m")
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id2 = userRepository.save(user2).getId();

        List<Integer> requestedUsers = List.of(id1, id2);

        mvc.perform(
                post(USERS_ENDPOINT + "/" + "disable")
                        .content(mapper.writeValueAsString(requestedUsers))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void disableUsers_unauthorized_returns403Status() throws Exception{
        User user1 = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id1 = userRepository.save(user1).getId();

        User user2 = User.builder()
                .userNick(TEST_NICK + "test")
                .email(TEST_VALID_EMAIL_ADDRESS + "m")
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(true)
                .build();

        int id2 = userRepository.save(user2).getId();

        List<Integer> requestedUsers = List.of(id1, id2);

        mvc.perform(
                post(USERS_ENDPOINT + "/" + "disable")
                        .content(mapper.writeValueAsString(requestedUsers))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void enableUsers_successfulResponse() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        User user1 = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(false)
                .build();

        int id1 = userRepository.save(user1).getId();

        User user2 = User.builder()
                .userNick(TEST_NICK + "test")
                .email(TEST_VALID_EMAIL_ADDRESS + "m")
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(false)
                .build();

        int id2 = userRepository.save(user2).getId();

        List<Integer> requestedUsers = List.of(id1, id2);

        mvc.perform(
                post(USERS_ENDPOINT + "/" + "enable")
                        .content(mapper.writeValueAsString(requestedUsers))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        User user1inDatabase = userRepository.findById(id1).get();
        User user2inDatabase = userRepository.findById(id2).get();

        assertTrue(user1inDatabase.getAccountNonLocked());
        assertTrue(user2inDatabase.getAccountNonLocked());
    }

    @Test
    void enableUsers_notAllowed_returns403Status() throws Exception{
        String[] studentTokenHeader = getStudentTokenHeader();

        User user1 = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(false)
                .build();

        int id1 = userRepository.save(user1).getId();

        User user2 = User.builder()
                .userNick(TEST_NICK + "test")
                .email(TEST_VALID_EMAIL_ADDRESS + "m")
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(false)
                .build();

        int id2 = userRepository.save(user2).getId();

        List<Integer> requestedUsers = List.of(id1, id2);

        mvc.perform(
                post(USERS_ENDPOINT + "/" + "enable")
                        .content(mapper.writeValueAsString(requestedUsers))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }

    @Test
    void enableUsers_unauthorized_returns403Status() throws Exception{
        User user1 = User.builder()
                .userNick(TEST_NICK)
                .email(TEST_VALID_EMAIL_ADDRESS)
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(false)
                .build();

        int id1 = userRepository.save(user1).getId();

        User user2 = User.builder()
                .userNick(TEST_NICK + "test")
                .email(TEST_VALID_EMAIL_ADDRESS + "m")
                .password(passwordEncoder.encode(TEST_VALID_PASSWORD))
                .accountNonLocked(false)
                .build();

        int id2 = userRepository.save(user2).getId();

        List<Integer> requestedUsers = List.of(id1, id2);

        mvc.perform(
                post(USERS_ENDPOINT + "/" + "enable")
                        .content(mapper.writeValueAsString(requestedUsers))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }
}
