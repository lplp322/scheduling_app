package nl.tudelft.sem.template.schedule.models;

import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * Model representing a request to schedule a request on a specific day.
 */
@Data
public class ScheduleRequestModel {
    private String netId;
    private int cpuUsage;
    private int gpuUsage;
    private int memoryUsage;
    private int day;
    private int month;
    private int year;
}
