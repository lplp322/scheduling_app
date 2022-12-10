package nl.tudelft.sem.template.schedule.models;

import lombok.Data;

import java.util.Date;

/**
 * Model representing a request to get the schedule of a specific day.
 */
@Data
public class GetScheduleRequestModel {
    private int day;
    private int month;
    private int year;
}
