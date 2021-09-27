package pl.adambaranowski.rsbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.adambaranowski.rsbackend.converter.WeekDaysConverter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.time.DayOfWeek;
import java.util.Set;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RecurrencePattern {
    @Convert(converter = WeekDaysConverter.class)
    private Set<DayOfWeek> weekDays;
    private Integer weekNum;
}
