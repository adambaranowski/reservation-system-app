package pl.adambaranowski.rsbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.adambaranowski.rsbackend.exception.NotAllowedException;
import pl.adambaranowski.rsbackend.exception.WrongDtoException;
import pl.adambaranowski.rsbackend.model.*;
import pl.adambaranowski.rsbackend.model.dto.CreateReservationRequestDto;
import pl.adambaranowski.rsbackend.model.dto.GetReservationRequestDto;
import pl.adambaranowski.rsbackend.model.dto.SingleReservationDto;
import pl.adambaranowski.rsbackend.repository.ReservationRepository;
import pl.adambaranowski.rsbackend.repository.RoomRepository;
import pl.adambaranowski.rsbackend.repository.UserRepository;
import pl.adambaranowski.rsbackend.service.utils.generator.GeneratorInputData;
import pl.adambaranowski.rsbackend.service.utils.generator.ReservationInstancesGenerator;
import pl.adambaranowski.rsbackend.validator.ReservationDtoValidator;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static pl.adambaranowski.rsbackend.validator.ValidationConstants.NULL_ROOM_NUMBER;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationDtoValidator validator;
    private final RoomRepository roomRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final ReservationInstancesGenerator generator;

    public List<SingleReservationDto> getReservationsForPeriodForRoom(GetReservationRequestDto requestDto) {

        LocalDate beginDate;
        LocalDate endDate;
        Integer roomNumber = requestDto.getRoomNumber();

        //validate
        try {
            beginDate = LocalDate.parse(requestDto.getBeginDate(), dateFormatter);
            endDate = LocalDate.parse(requestDto.getEndDate(), dateFormatter);
            if (roomNumber == null) {
                throw new WrongDtoException(List.of(NULL_ROOM_NUMBER));
            }
        } catch (DateTimeParseException e) {
            throw new WrongDtoException(List.of(e.getMessage()));
        }

        return generateReservationInstancesForRoom(roomNumber, beginDate, endDate);
    }

    public void deleteReservation(Integer reservationId) {
        //JwtFilter adds user to security context from token
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (userEmail == null || "anonymousUser".equals(userEmail) || userEmail.isBlank()) {
            throw new NoSuchElementException("Cannot find your user");
        }


        Reservation reservation = reservationRepository
                .findById(reservationId).orElseThrow(() -> new NoSuchElementException("Given reservation does not exist"));

        String userEmailInReservation = reservation.getUser().getEmail();
        List<String> authorities = reservation.getUser().getAuthorities().stream().map(Authority::getRole).collect(Collectors.toList());

        if (userEmailInReservation.equals(userEmail)) {
            reservationRepository.delete(reservation);
        } else {
            if (!authorities.contains(UserRole.ADMIN.name()))
                throw new NotAllowedException("It's not your reservation. You can't remove it unless you're admin");
        }

    }

    public void addReservation(CreateReservationRequestDto requestDto) {
        //JwtFilter adds user to security context from token
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        if (userEmail == null || "anonymousUser".equals(userEmail) || userEmail.isBlank()) {
            throw new NoSuchElementException("Cannot find your user");
        }


        validator.validateDto(requestDto);

        // Can be safely get, because JWT filter ensures that user exists
        User user = userRepository.findByEmail(userEmail).get();
        Room room = roomRepository.getByNumber(requestDto.getRoomNumber()).orElseThrow(() -> new NoSuchElementException("No such room"));

        checkIfUserAllowedToBookTheRoom(user, room);

        LocalDate beginDate = LocalDate.parse(requestDto.getBeginDate(), dateFormatter);

        //For non-recurring events endDate is beginDate
        LocalDate endDate = null;
        if (requestDto.getEndDate() != null && !requestDto.getEndDate().isBlank()) {
            endDate = LocalDate.parse(requestDto.getEndDate(), dateFormatter);
        }

        LocalTime beginTime = LocalTime.parse(requestDto.getBeginTime(), timeFormatter);
        LocalTime endTime = LocalTime.parse(requestDto.getEndTime());

        Set<DayOfWeek> daysOfWeek = requestDto.getDaysOfWeek().stream().map(DayOfWeek::of).collect(Collectors.toSet());
        RecurrencePattern recurrencePattern = null;
        if (!daysOfWeek.isEmpty()) {
            recurrencePattern = new RecurrencePattern();
            recurrencePattern.setWeekDays(daysOfWeek);
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .room(room)
                .beginDate(beginDate)
                .endDate(endDate)
                .beginTime(beginTime)
                .endTime(endTime)
                .recurrencePattern(recurrencePattern)
                .build();
        reservationRepository.save(reservation);
    }

    private void checkIfUserAllowedToBookTheRoom(User user, Room room) {
        Set<String> authorities = user.getAuthorities().stream().map(Authority::getRole).collect(Collectors.toSet());
        if (room.getRoomStatus() == RoomStatus.ONLY_TEACHER
                && !(authorities.contains(UserRole.TEACHER.name()) || authorities.contains(UserRole.ADMIN.name()))) {
            throw new NotAllowedException("This room is only for teachers");
        }
        if (room.getRoomStatus() == RoomStatus.ONLY_PRINCIPAL
                && !authorities.contains(UserRole.ADMIN.name())) {
            throw new NotAllowedException("This room is only for principal (admin)");
        }
    }

    private List<SingleReservationDto> generateReservationInstancesForRoom(
            Integer roomNumber, LocalDate requestedBeginDate, LocalDate requestedEndDate) {
        List<SingleReservationDto> reservationInstances = new ArrayList<>();

        List<GeneratorInputData> reservationsInput =
                reservationRepository.findAllForRoomStartingFromDate(roomNumber, requestedBeginDate, requestedEndDate)
                        .stream()
                        .map(reservation -> new GeneratorInputData(reservation, requestedBeginDate, requestedEndDate))
                        .collect(Collectors.toList());


        for (GeneratorInputData reservation : reservationsInput
        ) {
            reservationInstances
                    .addAll(generator.generateReservationInstances(reservation));
        }

        //sort just for clear view in tests
        return reservationInstances.stream()
                .sorted(
                        Comparator.comparing(reservation -> LocalDate.parse(reservation.getDate(), dateFormatter),
                                Comparator.naturalOrder()))
                .collect(Collectors.toList());

    }
}
