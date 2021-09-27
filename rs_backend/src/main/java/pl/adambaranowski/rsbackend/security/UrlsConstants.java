package pl.adambaranowski.rsbackend.security;

public final class UrlsConstants {
    public static String EQUIPMENT_ENDPOINT = "/equipment";
    public static String LOGIN_ENDPOINT = "/login";
    public static String RESERVATION_ENDPOINT = "/reservations";
    public static String ROOM_ENDPOINT = "/rooms";
    public static String USERS_ENDPOINT = "/users";

    private UrlsConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
