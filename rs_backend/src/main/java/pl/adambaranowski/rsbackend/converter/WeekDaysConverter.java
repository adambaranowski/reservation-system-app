package pl.adambaranowski.rsbackend.converter;

import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

@Component
public class WeekDaysConverter implements AttributeConverter<Set<DayOfWeek>, Byte> {

    private static final int NUMBER_OF_DAYS = 5;
    private static final int BIT_MASK = 1;
    private static final int VALID_DAY = 1;

    @Override
    public Byte convertToDatabaseColumn(Set<DayOfWeek> dayOfWeeks) {
        return dayOfWeeks == null ? null : dayOfWeeks.stream().map(DayOfWeek::getValue).reduce(0,
                (result, pos) -> result | (1 << pos)).byteValue();
    }

    @Override
    public Set<DayOfWeek> convertToEntityAttribute(Byte value) {
        if (value == null) {
            return null;
        }

        Set<DayOfWeek> daysOfWeek = new HashSet<>();
        for (int pos = 1; pos <= NUMBER_OF_DAYS; pos++) {
            int singleDayBit = (value >> pos) & BIT_MASK;
            if (singleDayBit == VALID_DAY) {
                daysOfWeek.add(DayOfWeek.of(pos));
            }
        }
        return daysOfWeek;
    }

}
