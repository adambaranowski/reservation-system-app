package pl.adambaranowski.rsbackend.tests.unit.mapper;

import org.junit.jupiter.api.Test;
import pl.adambaranowski.rsbackend.model.User;
import pl.adambaranowski.rsbackend.model.dto.UserResponseDto;
import pl.adambaranowski.rsbackend.service.utils.UserResponseMapper;
import pl.adambaranowski.rsbackend.service.utils.UserResponseMapperImpl;

import java.time.format.DateTimeFormatter;
import java.util.Collections;

import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserResponseMapperTest {
    UserResponseMapper mapper = new UserResponseMapperImpl();

    @Test
    void mapNullUser(){
        mapper.mapToDto(null);
    }

    @Test
    void mapUser_nullFields(){
        User user = new User();
        user.setId(null);
        user.setUserNick(null);
        user.setAccountNonExpired(null);
        user.setAuthorities(Collections.emptySet());
        user.setEmail(null);
        user.setAccountNonLocked(null);
        user.setCredentialsNonExpired(null);
        user.setJoinDateTime(null);
        user.setLastLoginDateTime(null);
        user.setPassword(null);
        user.setProfileImageUrl(null);
    }

    @Test
    void mapUser_nonEmptyFields(){
        User user = new User();
        user.setId(TEST_ID);
        user.setUserNick(TEST_NICK);
        user.setAccountNonExpired(TEST_ACCOUNT_NON_EXPIRED);
        user.setAuthorities(Collections.emptySet());
        user.setEmail(TEST_VALID_EMAIL_ADDRESS);
        user.setAccountNonLocked(TEST_ACCOUNT_NON_LOCKED);
        user.setCredentialsNonExpired(TEST_CREDENTIALS_NON_EXPIRED);
        user.setJoinDateTime(TEST_JOIN_DATE_TIME);
        user.setLastLoginDateTime(TEST_LAST_LOGIN_DATE_TIME);
        user.setPassword(TEST_VALID_PASSWORD);
        user.setProfileImageUrl(TEST_PROFILE_IMAGE_URL);

        UserResponseDto expectedResponseDto = new UserResponseDto();
        expectedResponseDto.setId(TEST_ID);
        expectedResponseDto.setUserNick(TEST_NICK);
        expectedResponseDto.setAccountNonExpired(TEST_ACCOUNT_NON_EXPIRED);
        expectedResponseDto.setAuthorities(Collections.emptyList());
        expectedResponseDto.setEmail(TEST_VALID_EMAIL_ADDRESS);
        expectedResponseDto.setAccountNonLocked(TEST_ACCOUNT_NON_LOCKED);
        expectedResponseDto.setCredentialsNonExpired(TEST_CREDENTIALS_NON_EXPIRED);
        expectedResponseDto.setJoinDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TEST_JOIN_DATE_TIME));
        expectedResponseDto.setLastLoginDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(TEST_LAST_LOGIN_DATE_TIME));

        UserResponseDto actualResponseDto = mapper.mapToDto(user);
        assertEquals(expectedResponseDto, actualResponseDto);
    }
}
