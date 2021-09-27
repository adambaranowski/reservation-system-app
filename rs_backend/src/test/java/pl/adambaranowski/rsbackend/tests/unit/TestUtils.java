package pl.adambaranowski.rsbackend.tests.unit;

import java.time.LocalDateTime;
import java.util.List;

public class TestUtils {

    public static final char TEST_STRING_GENERATOR = 'F';
    public static final int TEST_ROOM_NUMBER = 2137;
    public static final int TEST_WRONG_ROOM_NUMBER = 21370;
    public static final String TEST_ROOM_NAME = "testRoom";
    public static final List<Integer> TEST_WRONG_DAYS = List.of(6);
    public static final List<Integer> TEST_CORRECT_DAYS = List.of(3);
    public static final String TEST_PRIOR_DATE = "11-09-2001";
    public static final String TEST_LATER_DATE = "02-04-2005";
    public static final String TEST_PRIOR_TIME = "09:37";
    public static final String TEST_LATER_TIME = "21:37";
    public static final String TEST_VALID_PASSWORD = "TestPassword$123";
    public static final int TEST_ID = 10;
    public static final String TEST_NAME = "Item";
    public static final String TEST_NICK = "Steve";
    public static final boolean TEST_ACCOUNT_NON_EXPIRED = true;
    public static final boolean TEST_ACCOUNT_NON_LOCKED = true;
    public static final boolean TEST_CREDENTIALS_NON_EXPIRED = true;
    public static final LocalDateTime TEST_JOIN_DATE_TIME = LocalDateTime.MIN;
    public static final LocalDateTime TEST_LAST_LOGIN_DATE_TIME = LocalDateTime.now();
    public static final String TEST_PROFILE_IMAGE_URL = "http://graph.facebook.com/702855/picture";
    public static final String TEST_VALID_EMAIL_ADDRESS = "someone@example.com";

    public enum InvalidPasswords {

        NO_DIGIT("TestPassword$"),
        NO_LOWERCASE("TESTPASSWORD$123"),
        NO_UPPERCASE("testpassword$123"),
        NO_SPECIAL("TestPassword123"),
        BLANK_SPACE("Test Password$123"),
        TOO_SHORT("TsPa$1");

        String password;

        public String getPassword() {
            return password;
        }

        InvalidPasswords(String password) {
            this.password = password;
        }
    }

    public enum InvalidEmailAddresses {

        NO_AT("someoneexample.com"),
        NO_DOT("someone@example"),
        TOO_LONG_SUFFIX("someone@example.comcomcomcom"),
        TOO_SHORT_SUFFIX("someone@example.c"),
        NO_SUFFIX("someone@example."),
        NO_DOMAIN("someone@.com"),
        NO_NAME("@example.com"),
        BLANK_SPACE("someone @example.com");

        String email;

        public String getEmail() {
            return email;
        }

        InvalidEmailAddresses(String email) {
            this.email = email;
        }
    }

    public static final List<String> TEST_AUTHORITIES = List.of(
            "admin",
            "user"
    );



    public static String generateTestString(int length){
        return String.valueOf(TEST_STRING_GENERATOR).repeat(Math.max(0, length));
    }

    private TestUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
