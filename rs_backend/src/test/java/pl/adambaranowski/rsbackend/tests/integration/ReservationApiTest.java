package pl.adambaranowski.rsbackend.tests.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.adambaranowski.rsbackend.model.*;
import pl.adambaranowski.rsbackend.model.dto.*;
import pl.adambaranowski.rsbackend.repository.ReservationRepository;
import pl.adambaranowski.rsbackend.service.utils.generator.GeneratorInputData;
import pl.adambaranowski.rsbackend.service.utils.generator.ReservationInstancesGenerator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.adambaranowski.rsbackend.security.UrlsConstants.RESERVATION_ENDPOINT;
import static pl.adambaranowski.rsbackend.tests.unit.TestUtils.*;
import static pl.adambaranowski.rsbackend.validator.ValidationConstants.*;

public class ReservationApiTest extends BaseIntegrationTestClass{

    @Autowired
    ReservationRepository reservationRepository;

    private final ReservationInstancesGenerator generator = new ReservationInstancesGenerator();
    private static final String TEACHER_ONLY = "This room is only for teachers";
    private static final String RESERVATION_NOT_FOUND = "Given reservation does not exist";

    @Test
    void createReservation_successResponse() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        CreateReservationRequestDto createReservationRequestDto = CreateReservationRequestDto.builder()
                .roomNumber(TEST_ROOM_NUMBER)
                .beginDate(TEST_PRIOR_DATE)
                .beginTime(TEST_PRIOR_TIME)
                .endDate(TEST_LATER_DATE)
                .endTime(TEST_LATER_TIME)
                .daysOfWeek(TEST_CORRECT_DAYS)
                .build();

        mvc.perform(
                post(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(createReservationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);
        LocalTime beginTime = LocalTime.parse(TEST_PRIOR_TIME, timeFormatter);
        LocalTime endTime = LocalTime.parse(TEST_LATER_TIME, timeFormatter);

        Reservation actualReservation =
                (reservationRepository.findAllForRoomStartingFromDate(TEST_ROOM_NUMBER, beginDate, endDate)).get(0);
        actualReservation.setId(null);

        RecurrencePattern pattern = new RecurrencePattern();
        DayOfWeek wednesday = DayOfWeek.of(TEST_CORRECT_DAYS.get(0));
        pattern.setWeekDays(Set.of(wednesday));

        Reservation expectedReservation = Reservation.builder()
                .room(attachedRoom)
                .beginTime(beginTime)
                .endTime(endTime)
                .beginDate(beginDate)
                .endDate(endDate)
                .recurrencePattern(pattern)
                .user(userRepository.findByEmail(ADMIN_EMAIL).get())
                .build();

        assertEquals(expectedReservation, actualReservation);
    }

    @Test
    void createReservation_notAllowed_returns403Status() throws Exception{
        String[] studentTokenHeader = getStudentTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.ONLY_TEACHER)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        roomRepository.save(room);

        CreateReservationRequestDto createReservationRequestDto = CreateReservationRequestDto.builder()
                .roomNumber(TEST_ROOM_NUMBER)
                .beginDate(TEST_PRIOR_DATE)
                .beginTime(TEST_PRIOR_TIME)
                .endDate(TEST_LATER_DATE)
                .endTime(TEST_LATER_TIME)
                .daysOfWeek(TEST_CORRECT_DAYS)
                .build();

        String body = mvc.perform(
                post(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(createReservationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(studentTokenHeader[AUTH_HEADER_NAME], studentTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);

        List<Reservation> actualReservations =
                reservationRepository.findAllForRoomStartingFromDate(TEST_ROOM_NUMBER, beginDate, endDate);

        assertTrue(actualReservations.isEmpty());
        assertEquals(body, TEACHER_ONLY);
    }

    @Test
    void createReservation_emptyParams_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        roomRepository.save(room);

        CreateReservationRequestDto createReservationRequestDto = CreateReservationRequestDto.builder()
                .roomNumber(null)
                .beginDate("")
                .beginTime("")
                .endDate("")
                .endTime("")
                .daysOfWeek(Collections.emptyList())
                .build();

        String body = mvc.perform(
                post(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(createReservationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> expectedErrorsList = List.of(
                NULL_ROOM_NUMBER,
                EMPTY_DATE,
                EMPTY_TIME
        );

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);

        List<Reservation> actualReservations =
                reservationRepository.findAllForRoomStartingFromDate(TEST_ROOM_NUMBER, beginDate, endDate);

        assertTrue(actualReservations.isEmpty());
        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void createReservation_wrongParams_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        roomRepository.save(room);

        CreateReservationRequestDto createReservationRequestDto = CreateReservationRequestDto.builder()
                .roomNumber(TEST_WRONG_ROOM_NUMBER)
                .beginDate(TEST_LATER_DATE)
                .beginTime(TEST_LATER_TIME)
                .endDate(TEST_PRIOR_DATE)
                .endTime(TEST_PRIOR_TIME)
                .daysOfWeek(TEST_WRONG_DAYS)
                .build();

        String body = mvc.perform(
                post(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(createReservationRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<String> expectedErrorsList = List.of(
                BEGIN_DATE_AFTER_END_DATE,
                BEGIN_TIME_AFTER_END_TIME,
                TEST_WRONG_DAYS.get(0) + ALLOWED_DAYS_MESSAGE
        );

        List<String> actualErrorsList = Arrays.asList(body.substring(1, body.length() - 1).split(", "));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);

        List<Reservation> actualReservations =
                reservationRepository.findAllForRoomStartingFromDate(TEST_ROOM_NUMBER, beginDate, endDate);

        assertTrue(actualReservations.isEmpty());
        assertEquals(expectedErrorsList, actualErrorsList);
    }

    @Test
    void getReservations_returnReservationList() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);
        LocalDate reservationBeginDate = beginDate.plusMonths(1);
        LocalDate reservationEndDate = endDate.minusMonths(1);
        LocalTime reservationBeginTime = LocalTime.parse(TEST_PRIOR_TIME, timeFormatter);
        LocalTime reservationEndTime = LocalTime.parse(TEST_LATER_TIME, timeFormatter);

        RecurrencePattern pattern = new RecurrencePattern();
        DayOfWeek wednesday = DayOfWeek.of(TEST_CORRECT_DAYS.get(0));
        pattern.setWeekDays(Set.of(wednesday));

        Reservation reservation = Reservation.builder()
                .room(attachedRoom)
                .beginTime(reservationBeginTime)
                .endTime(reservationEndTime)
                .beginDate(reservationBeginDate)
                .endDate(reservationEndDate)
                .recurrencePattern(pattern)
                .user(userRepository.findByEmail(ADMIN_EMAIL).get())
                .build();

        reservationRepository.save(reservation);

        GeneratorInputData inputData = new GeneratorInputData(reservation, beginDate, endDate);

        List<SingleReservationDto> expectedDtos = generator.generateReservationInstances(inputData);

        GetReservationRequestDto requestDto = GetReservationRequestDto.builder()
                .beginDate(TEST_PRIOR_DATE)
                .endDate(TEST_LATER_DATE)
                .roomNumber(TEST_ROOM_NUMBER)
                .build();

        String body = mvc.perform(
                get(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<SingleReservationDto> actualDtos = Arrays.asList(mapper.readValue(body, SingleReservationDto[].class));

        assertEquals(expectedDtos, actualDtos);
    }

    @Test
    void getReservations_emptyRoomNumber_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        roomRepository.save(room);

        GetReservationRequestDto requestDto = GetReservationRequestDto.builder()
                .beginDate(TEST_PRIOR_DATE)
                .endDate(TEST_LATER_DATE)
                .roomNumber(null)
                .build();

        String expectedError = mvc.perform(
                get(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(NULL_ROOM_NUMBER ,expectedError.substring(1, expectedError.length() - 1));
    }

    @Test
    void getReservations_emptyDate_returns400Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);
        LocalDate reservationBeginDate = beginDate.plusMonths(1);
        LocalDate reservationEndDate = endDate.minusMonths(1);
        LocalTime reservationBeginTime = LocalTime.parse(TEST_PRIOR_TIME, timeFormatter);
        LocalTime reservationEndTime = LocalTime.parse(TEST_LATER_TIME, timeFormatter);

        RecurrencePattern pattern = new RecurrencePattern();
        DayOfWeek wednesday = DayOfWeek.of(TEST_CORRECT_DAYS.get(0));
        pattern.setWeekDays(Set.of(wednesday));

        Reservation reservation = Reservation.builder()
                .room(attachedRoom)
                .beginTime(reservationBeginTime)
                .endTime(reservationEndTime)
                .beginDate(reservationBeginDate)
                .endDate(reservationEndDate)
                .recurrencePattern(pattern)
                .user(userRepository.findByEmail(ADMIN_EMAIL).get())
                .build();

        reservationRepository.save(reservation);

        GetReservationRequestDto requestDto = GetReservationRequestDto.builder()
                .beginDate("")
                .endDate("")
                .roomNumber(TEST_ROOM_NUMBER)
                .build();

        mvc.perform(
                get(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getReservations_unauthorized_return403Status() throws Exception{
        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);
        LocalDate reservationBeginDate = beginDate.plusMonths(1);
        LocalDate reservationEndDate = endDate.minusMonths(1);
        LocalTime reservationBeginTime = LocalTime.parse(TEST_PRIOR_TIME, timeFormatter);
        LocalTime reservationEndTime = LocalTime.parse(TEST_LATER_TIME, timeFormatter);

        RecurrencePattern pattern = new RecurrencePattern();
        DayOfWeek wednesday = DayOfWeek.of(TEST_CORRECT_DAYS.get(0));
        pattern.setWeekDays(Set.of(wednesday));

        Reservation reservation = Reservation.builder()
                .room(attachedRoom)
                .beginTime(reservationBeginTime)
                .endTime(reservationEndTime)
                .beginDate(reservationBeginDate)
                .endDate(reservationEndDate)
                .recurrencePattern(pattern)
                .user(userRepository.findByEmail(ADMIN_EMAIL).get())
                .build();

        reservationRepository.save(reservation);

        GetReservationRequestDto requestDto = GetReservationRequestDto.builder()
                .beginDate(TEST_PRIOR_DATE)
                .endDate(TEST_LATER_DATE)
                .roomNumber(TEST_ROOM_NUMBER)
                .build();

        mvc.perform(
                get(RESERVATION_ENDPOINT)
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void deleteReservation_successResponse() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);
        LocalDate reservationBeginDate = beginDate.plusMonths(1);
        LocalDate reservationEndDate = endDate.minusMonths(1);
        LocalTime reservationBeginTime = LocalTime.parse(TEST_PRIOR_TIME, timeFormatter);
        LocalTime reservationEndTime = LocalTime.parse(TEST_LATER_TIME, timeFormatter);

        RecurrencePattern pattern = new RecurrencePattern();
        DayOfWeek wednesday = DayOfWeek.of(TEST_CORRECT_DAYS.get(0));
        pattern.setWeekDays(Set.of(wednesday));

        Reservation reservation = Reservation.builder()
                .room(attachedRoom)
                .beginTime(reservationBeginTime)
                .endTime(reservationEndTime)
                .beginDate(reservationBeginDate)
                .endDate(reservationEndDate)
                .recurrencePattern(pattern)
                .user(userRepository.findByEmail(ADMIN_EMAIL).get())
                .build();

        int id = reservationRepository.save(reservation).getId();

        mvc.perform(
                delete(RESERVATION_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<Reservation> reservations =
                reservationRepository.findAllForRoomStartingFromDate(TEST_ROOM_NUMBER, beginDate, endDate);

        assertTrue(reservations.isEmpty());
    }

    @Test
    void deleteReservation_notFound_returns404Status() throws Exception{
        String[] adminTokenHeader = getAdminTokenHeader();

        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);
        LocalDate reservationBeginDate = beginDate.plusMonths(1);
        LocalDate reservationEndDate = endDate.minusMonths(1);
        LocalTime reservationBeginTime = LocalTime.parse(TEST_PRIOR_TIME, timeFormatter);
        LocalTime reservationEndTime = LocalTime.parse(TEST_LATER_TIME, timeFormatter);

        RecurrencePattern pattern = new RecurrencePattern();
        DayOfWeek wednesday = DayOfWeek.of(TEST_CORRECT_DAYS.get(0));
        pattern.setWeekDays(Set.of(wednesday));

        Reservation reservation = Reservation.builder()
                .room(attachedRoom)
                .beginTime(reservationBeginTime)
                .endTime(reservationEndTime)
                .beginDate(reservationBeginDate)
                .endDate(reservationEndDate)
                .recurrencePattern(pattern)
                .user(userRepository.findByEmail(ADMIN_EMAIL).get())
                .build();

        int id = reservationRepository.save(reservation).getId();
        int wrongId = id + 1;

        String actualError = mvc.perform(
                delete(RESERVATION_ENDPOINT + "/" + wrongId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(adminTokenHeader[AUTH_HEADER_NAME], adminTokenHeader[AUTH_HEADER_VALUE])
        )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(RESERVATION_NOT_FOUND, actualError);
    }

    @Test
    void deleteReservation_unauthorized_returns403Status() throws Exception{
        Room room = Room.builder()
                .roomStatus(RoomStatus.NORMAL)
                .number(TEST_ROOM_NUMBER)
                .description(TEST_ROOM_NAME)
                .reservations(null)
                .build();

        Room attachedRoom = roomRepository.save(room);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate beginDate = LocalDate.parse(TEST_PRIOR_DATE, dateFormatter);
        LocalDate endDate = LocalDate.parse(TEST_LATER_DATE, dateFormatter);
        LocalDate reservationBeginDate = beginDate.plusMonths(1);
        LocalDate reservationEndDate = endDate.minusMonths(1);
        LocalTime reservationBeginTime = LocalTime.parse(TEST_PRIOR_TIME, timeFormatter);
        LocalTime reservationEndTime = LocalTime.parse(TEST_LATER_TIME, timeFormatter);

        RecurrencePattern pattern = new RecurrencePattern();
        DayOfWeek wednesday = DayOfWeek.of(TEST_CORRECT_DAYS.get(0));
        pattern.setWeekDays(Set.of(wednesday));

        Reservation reservation = Reservation.builder()
                .room(attachedRoom)
                .beginTime(reservationBeginTime)
                .endTime(reservationEndTime)
                .beginDate(reservationBeginDate)
                .endDate(reservationEndDate)
                .recurrencePattern(pattern)
                .user(userRepository.findByEmail(ADMIN_EMAIL).get())
                .build();

        int id = reservationRepository.save(reservation).getId();

        mvc.perform(
                delete(RESERVATION_ENDPOINT + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse();
    }
}
