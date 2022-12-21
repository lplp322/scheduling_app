package nl.tudelft.sem.waitinglist.controllers;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.List;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import nl.tudelft.sem.waitinglist.domain.Request;
import nl.tudelft.sem.waitinglist.domain.Resources;
import nl.tudelft.sem.waitinglist.domain.WaitingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class WaitingListController {
    private final transient WaitingList waitingList;
    private final transient Clock clock;

    @Autowired
    public WaitingListController(WaitingList waitingList, Clock clock) {
        this.waitingList = waitingList;
        this.clock = clock;
    }

    /**
     * Adds a request to waiting list.
     *
     * @param requestModel request model
     * @return request id
     */
    @PostMapping("/add-request")
    public ResponseEntity<AddResponseModel> addRequest(@RequestBody RequestModel requestModel) {
        try {
            LocalDateTime currentDateTime = LocalDateTime.ofInstant(clock.instant(), clock.getZone());
            Request request = new Request(requestModel, currentDateTime);
            Long id = waitingList.addRequest(request);
            return ResponseEntity.ok(new AddResponseModel(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    /**
     * Gets a list of all the requests of a faculty.
     *
     * @param faculty - String - faculty for which the request is.
     * @return List of Requests - list of all the pending requests for the faculty
     */
    @GetMapping("/get-requests-by-faculty")
    public ResponseEntity<List<Request>> getRequestsByFaculty(@RequestBody String faculty) {
        return ResponseEntity.ok(waitingList.getAllRequestsByFaculty(faculty));
    }

    /**
     * Rejects a request.
     *
     * @param id request id
     * @return response
     */
    @DeleteMapping("/reject-request")
    public ResponseEntity<String> rejectRequest(@RequestBody Long id) {
        try {
            waitingList.rejectRequest(id);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * In the last 6 hours of each day tries to schedule the pending requests with deadline tomorrow.
     * It will try the request with the lowest id first.
     * If a request can't be scheduled, only smaller requests are tried to schedule.
     */
    @Scheduled(cron = "0 */5 18-23 * * *")
    public void tryToScheduleInLastSixHours() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Request> requestsForTomorrow = waitingList.getRequestWithDeadlineOnDate(tomorrow);
        Resources resourcesThatAreTooBig = null;
        for (int i = 0; i < requestsForTomorrow.size(); i++) {
            Request request = requestsForTomorrow.get(i);
            if (resourcesThatAreTooBig == null || request.getResources().isResourceSmaller(resourcesThatAreTooBig)) {
                request.setPlannedDate(tomorrow);
                //try to schedule in scheduler
                //if scheduled:
                waitingList.rejectRequest(request.getId()); //rejectRequest = removeRequest in other branch.
                //else:
                resourcesThatAreTooBig = request.getResources();
            }
        }
    }


}
