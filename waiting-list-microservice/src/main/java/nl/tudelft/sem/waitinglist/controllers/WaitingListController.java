package nl.tudelft.sem.waitinglist.controllers;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import nl.tudelft.sem.waitinglist.domain.Request;
import nl.tudelft.sem.waitinglist.domain.WaitingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            LocalDate currentDate = LocalDate.ofInstant(clock.instant(), clock.getZone());
            Request request = new Request(requestModel, currentDate);
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
            waitingList.removeRequest(id);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Accepts a request.
     *
     * @param objectNode - ObjectNode containing the id and the planned-date of the accepted request.
     * @return response
     */
    @PostMapping("/accept-request")
    public ResponseEntity<Request> acceptRequest(@RequestBody ObjectNode objectNode) {
        try {
            Long id = objectNode.get("id").asLong();
            LocalDate plannedDate = LocalDate.parse(objectNode.get ("localDate").asText());
            Request acceptedRequest = waitingList.getRequestById(id);
            LocalDate currentDate = LocalDate.ofInstant(clock.instant(), clock.getZone());
            acceptedRequest.setPlannedDate(plannedDate, currentDate);
            //forward to scheduler
            //if scheduler approves:
            waitingList.removeRequest(id);

            return ResponseEntity.ok(acceptedRequest);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
//        return ResponseEntity.ok().build();
    }
}
