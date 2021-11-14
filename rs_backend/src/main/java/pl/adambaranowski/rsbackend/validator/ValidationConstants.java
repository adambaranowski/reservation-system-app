package pl.adambaranowski.rsbackend.validator;

public final class ValidationConstants {
    public static int ROOM_DESCRIPTION_MAX_LENGTH = 350;
    public static int NICK_MAX_LENGTH = 30;

    public static String WRONG_NICK = "User must have not-empty nick of max length: " + NICK_MAX_LENGTH;
    public static String WRONG_EMAIL = "User must have not-empty valid email";

    public static String NULL_ROOM_NUMBER = "Room number cannot be empty or null";
    public static String NULL_ROOM_STATUS = "Room status cannot be empty or null";
    public static String BLANK_DESCRIPTION = "Room description cannot be blank";
    public static String TOO_LONG_DESCRIPTION = "Room description can be include" + ROOM_DESCRIPTION_MAX_LENGTH + " chars";
    public static String BEGIN_DATE_AFTER_END_DATE = "Begin date is after end date";
    public static String BEGIN_TIME_AFTER_END_TIME = "Begin time is after end time";
    public static String EMPTY_DATE = "Begin date and End date must not be empty";
    public static String EMPTY_TIME = "Begin time and End time must not be empty";
    public static String ALLOWED_DAYS_MESSAGE = " is not not a valid day. Only Monday-Friday allowed (1-5)";
    public static String WRONG_ROOM_NUMBER = "Room number can must be in range 0-1000";
    public static String NULL_DAYS = "Days cannot be null. This field can be an empty list";
    public static int DESCRIPTION_MAX_LENGTH = 250;
    public static int NAME_MAX_LENGTH = 100;
    public static String EMPTY_NAME = "Equipment must have not-empty name";
    public static String EMPTY_DESCRIPTION = "Equipment must have not-empty description";
    public static String TOO_LONG_EQUIPMENT_DESCRIPTION = "Equipment description can be max " + DESCRIPTION_MAX_LENGTH + " chars long";
    public static String TOO_LONG_NAME = "Equipment name can be max " + NAME_MAX_LENGTH + " chars long";

    private ValidationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
