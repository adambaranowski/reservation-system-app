package pl.adambaranowski.rsbackend;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.adambaranowski.rsbackend.model.*;
import pl.adambaranowski.rsbackend.repository.*;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        if (authorityRepository.count() == 0) {
            loadSecurityData();
        }
        loadRoomsWithEquipment();
    }

    private void loadSecurityData() {
        Authority teacher = authorityRepository.save(Authority.builder().role(UserRole.TEACHER.name()).build());
        Authority student = authorityRepository.save(Authority.builder().role(UserRole.STUDENT.name()).build());
        Authority admin = authorityRepository.save(Authority.builder().role(UserRole.ADMIN.name()).build());

        userRepository.save(User.builder()
                .email("teacher@gmail.com")
                .password(passwordEncoder.encode("teacher"))
                .authority(teacher)
                .userNick("teacher student")
                .joinDateTime(LocalDateTime.now())
                .build());

        userRepository.save(User.builder()
                .email("student@gmail.com")
                .password(passwordEncoder.encode("student"))
                .authority(student)
                .userNick("student")
                .joinDateTime(LocalDateTime.now())
                .build());

        userRepository.save(User.builder()
                .email("admin@gmail.com")
                .password(passwordEncoder.encode("admin"))
                .authority(admin)
                .userNick("admin")
                .joinDateTime(LocalDateTime.now())
                .build());
    }

    private void loadRoomsWithEquipment() {
        Equipment smallPneumaticOrgan = Equipment.builder()
                .name("Pneumatic Organ")
                .description("II+P, 8 stops, pneumatic tracture organ")
                .build();

        Equipment smallMechanicOrgan = Equipment.builder()
                .name("Mechanic Walcker Organ")
                .description("II+P, 7 stops, mechanic tracture organ")
                .build();

        Equipment calisiaPiano = Equipment.builder()
                .name("Calisia Piano")
                .description("Small piano")
                .build();

        Equipment yamahaSmallGrand = Equipment.builder()
                .name("Yamaha Small Grand Piano")
                .description("Yamaha Small Grand Piano, well-tuned")
                .build();


        Room room21 = Room.builder()
                .number(21)
                .description("Small organ room on 2nd floor")
                .roomStatus(RoomStatus.NORMAL)
                .build();
        room21.addEquipment(smallPneumaticOrgan);

        Room room25 = Room.builder()
                .number(25)
                .description("Small organ-piano room on 2nd floor")
                .roomStatus(RoomStatus.NORMAL)
                .build();
        room25.addEquipment(smallMechanicOrgan);
        room25.addEquipment(calisiaPiano);

        Room room22 = Room.builder()
                .number(22)
                .description("Grand Piano room on 2nd floor")
                .roomStatus(RoomStatus.ONLY_TEACHER)
                .build();
        room22.addEquipment(yamahaSmallGrand);

        roomRepository.save(room21);
        roomRepository.save(room22);
        roomRepository.save(room25);

        Reservation recurringReservation = new Reservation();
        recurringReservation.setRoom(room22);
        recurringReservation.setBeginDate(LocalDate.of(2021, 10, 10));
        recurringReservation.setEndDate(LocalDate.of(2021, 10, 20));
        recurringReservation.setBeginTime(LocalTime.of(12, 30));
        recurringReservation.setEndTime(LocalTime.of(14, 0));

        RecurrencePattern recurrencePattern = new RecurrencePattern();
        recurrencePattern.setWeekDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));

        recurringReservation.setRecurrencePattern(recurrencePattern);
        recurringReservation.setUser(userRepository.findByEmail("admin@gmail.com").get());

        reservationRepository.save(recurringReservation);

        Reservation nonRecurring = new Reservation();
        nonRecurring.setRoom(room22);
        nonRecurring.setBeginDate(LocalDate.of(2021, 10, 14));
        nonRecurring.setBeginTime(LocalTime.of(9, 45));
        nonRecurring.setEndTime(LocalTime.of(11, 0));

        nonRecurring.setUser(userRepository.findByEmail("admin@gmail.com").get());

        reservationRepository.save(nonRecurring);

    }

}
