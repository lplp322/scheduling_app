package nl.tudelft.sem.common.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Model representing a request to get the schedule of a specific day.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateModel {
    private LocalDate date;
}
