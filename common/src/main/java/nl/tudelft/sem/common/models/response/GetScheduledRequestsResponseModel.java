package nl.tudelft.sem.common.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Model representing a response returning requests. This could be returning scheduled requests in the
 * Schedule microservice or pending requests in de Waiting List microservice.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetScheduledRequestsResponseModel {
    private LocalDate date; //Can be used to indicate when the deadline or planned date of the requests is.
    private List<RequestModelSchedule> requests;
}
