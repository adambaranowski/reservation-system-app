package pl.adambaranowski.rsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Reservation {
    @ManyToOne
    private Room room;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne()
    private User user;
    private LocalDate beginDate;
    private LocalDate endDate;

    private LocalTime beginTime;
    private LocalTime endTime;

    @Embedded
    private RecurrencePattern recurrencePattern;


}
