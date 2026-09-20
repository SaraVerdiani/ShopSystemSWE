package store.domain_model.workers;

import java.time.LocalDate;
import java.time.LocalTime;

public class Shift {
    private LocalDate workingDay;
    private ShiftType shiftType;

    public enum ShiftType {
        MORNING(LocalTime.of(7,0), LocalTime.of(14,0)),
        AFTERNOON(LocalTime.of(12,0), LocalTime.of(20,0)),
        NIGHT(LocalTime.of(22,0), LocalTime.of(6,0));

        private final LocalTime start;
        private final LocalTime end;

        ShiftType(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }

        public LocalTime getStart() {
            return start;
        }

        public LocalTime getEnd() {
            return end;
        }

    };

    public Shift(LocalDate workingDay, ShiftType shiftType) {
        this.workingDay = workingDay;
        this.shiftType = shiftType;
    }

    public LocalDate getWorkingDay() {
        return workingDay;
    }

    public ShiftType getShiftType() {
        return shiftType;
    }

}
