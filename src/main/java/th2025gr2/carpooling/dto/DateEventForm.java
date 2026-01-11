package th2025gr2.carpooling.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class DateEventForm {

    @Getter
    @Setter
    private LocalDateTime dateStartTime;

    @Getter
    @Setter
    private LocalDateTime dateStartEnd;
}