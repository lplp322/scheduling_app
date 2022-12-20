package nl.tudelft.sem.common.models.request;

import lombok.Data;

import java.time.LocalDate;

/**
 * Model representing a request to get the schedule of a specific day.
 */
@Data
public class DateModel {
    private LocalDate date;
}