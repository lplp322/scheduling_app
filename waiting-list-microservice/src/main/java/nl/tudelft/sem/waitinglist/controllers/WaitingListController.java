package nl.tudelft.sem.waitinglist.controllers;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.List;

import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.waitinglist.authentication.AuthManager;
import nl.tudelft.sem.waitinglist.domain.Request;
import nl.tudelft.sem.waitinglist.domain.WaitingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.jaas.AuthorityGranter;
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
    private final transient AuthManager authManager;

    /**
     * WaitingListController constructor, this should never be called manually, spring should handle this instead.
     *
     * @param authManager AuthManager instance for this controller
     * @param waitingList WaitingList instance for this controller
     * @param clock Clock instance for this controller
     */
    @Autowired
    public WaitingListController(AuthManager authManager, WaitingList waitingList, Clock clock) {
        this.authManager = authManager;
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
    public ResponseEntity<AddResponseModel> addRequest(@RequestBody RequestModelWaitingList requestModel) {
        try {
            if (authManager == null || !authManager.getNetId().equals(requestModel.getName()) || authManager.getRoles()
                    .stream().noneMatch(a -> a.getAuthority().contains("employee_" + requestModel.getFaculty()))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
            }
            LocalDateTime currentDateTime = LocalDateTime.ofInstant(clock.instant(), clock.getZone());
            Request request = new Request(requestModel, currentDateTime);
            Long id = waitingList.addRequest(request);
            return ResponseEntity.ok(new AddResponseModel(id));
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        if (authManager == null || authManager.getRoles().stream()
            .noneMatch(a -> a.getAuthority().contains("admin_" + faculty))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
        }
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
        Request request = waitingList.getRequestById(id); //NOPMD
        if (request != null && (authManager == null || authManager.getRoles().stream()
                .noneMatch(a -> a.getAuthority().contains("admin_" + request.getFaculty())))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
        }
        try {
            waitingList.rejectRequest(id);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }
}
