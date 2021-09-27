package pl.adambaranowski.rsbackend.service.utils.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.adambaranowski.rsbackend.model.dto.SingleReservationDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationInstancesGenerator {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    public List<SingleReservationDto> generateReservationInstances(GeneratorInputData inputData) {
        List<SingleReservationDto> reservationInstances = new ArrayList<>();

            if (inputData.getWeekDays() == null) {
                reservationInstances.add(getSingleReservationForNonRecurring(inputData));
            } else {
                reservationInstances.addAll(getSingleReservationForRecurring(inputData));
            }

        //sort just for clear view in tests
        return reservationInstances.stream()
                .sorted(
                        Comparator.comparing(reservation -> LocalDate.parse(reservation.getDate(), dateFormatter),
                                Comparator.naturalOrder()))
                .collect(Collectors.toList());

    }

    private SingleReservationDto getSingleReservationForNonRecurring(GeneratorInputData input) {
        return SingleReservationDto.builder()
                .reservationId(input.getReservationId())
                .date(input.getReservation().getBeginDate().format(dateFormatter))
                .userNick(input.getUser().getUserNick())
                .beginTime(input.getReservation().getBeginTime().format(timeFormatter))
                .endTime(input.getReservation().getEndTime().format(timeFormatter))
                .build();
    }

    private List<SingleReservationDto> getSingleReservationForRecurring(GeneratorInputData inputData) {
        List<SingleReservationDto> reservationInstances = new ArrayList<>();

        String userNick = inputData.getUser().getUserNick();
        int reservationId = inputData.getReservationId();
        String beginTime = inputData.getReservation().getBeginTime().format(timeFormatter);
        String endTime = inputData.getReservation().getEndTime().format(timeFormatter);

        //End date in reservation pattern
        LocalDate endDate = inputData.getEndDate();

        LocalDate currentDay = inputData.getStartDate();
        Set<DayOfWeek> weekDays = inputData.getWeekDays();

        while (currentDay.isBefore(endDate)) {
            if (weekDays.contains(currentDay.getDayOfWeek())) {
                SingleReservationDto dto = SingleReservationDto.builder()
                        .reservationId(reservationId)
                        .userNick(userNick)
                        .date(currentDay.format(dateFormatter))
                        .beginTime(beginTime)
                        .endTime(endTime).build();

                reservationInstances.add(dto);

            }
            currentDay = currentDay.plusDays(1);
        }
        return reservationInstances;
    }
}
