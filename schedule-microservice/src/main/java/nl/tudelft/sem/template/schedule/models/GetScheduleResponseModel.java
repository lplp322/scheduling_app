package nl.tudelft.sem.template.schedule.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;

import java.util.Date;
import java.util.List;

/**
 * Model representing a schedule response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetScheduleResponseModel {
    Date date;
    List<ScheduledRequest> requests;
}
