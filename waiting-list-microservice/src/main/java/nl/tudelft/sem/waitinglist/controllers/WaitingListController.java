package nl.tudelft.sem.waitinglist.controllers;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.waitinglist.authentication.AuthManager;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import nl.tudelft.sem.waitinglist.domain.Request;
import nl.tudelft.sem.waitinglist.domain.Resources;
import nl.tudelft.sem.waitinglist.domain.WaitingList;
import nl.tudelft.sem.waitinglist.external.SchedulerService;
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
    private final transient AuthManager authManager;
    private final transient SchedulerService schedulerService;

    /**
     * WaitingListController constructor, this should never be called manually, spring should handle this instead.
     *
     * @param authManager AuthManager instance for this controller
     * @param waitingList WaitingList instance for this controller
     * @param clock Clock instance for this controller
     * @param schedulerService SchedulerService instance for this controller
     */
    @Autowired
    public WaitingListController(AuthManager authManager, WaitingList waitingList,
                                 Clock clock, SchedulerService schedulerService) {
        this.authManager = authManager;
        this.waitingList = waitingList;
        this.clock = clock;
        this.schedulerService = schedulerService;
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
     * @return String - list of all the pending requests for the faculty mapped to JSON format.
     */
    @GetMapping("/get-requests-by-faculty")
    public ResponseEntity<String> getRequestsByFaculty(@RequestBody String faculty) {
        if (authManager == null || authManager.getRoles().stream()
            .noneMatch(a -> a.getAuthority().contains("admin_" + faculty))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
        }
        ObjectMapper mapper = new ObjectMapper();
        List<Request> requestListByFaculty = waitingList.getAllRequestsByFaculty(faculty);
        try {
            String json = mapper.writeValueAsString(requestListByFaculty);
            return ResponseEntity.ok(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().build();
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
            Request request = waitingList.getRequestById(id); //NOPMD
            if (request != null && (authManager == null || authManager.getRoles().stream()
                    .noneMatch(a -> a.getAuthority().contains("admin_" + request.getFaculty())))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
            }
            waitingList.removeRequest(id);
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



    /**
     * Accepts a request: sends request to the scheduler.
     * if scheduled the request is removed from the waiting-list
     *
     * @param objectNode - ObjectNode containing the id and the planned-date of the accepted request.
     * @return response - ok() when accepted and scheduled
     */
    @PostMapping("/accept-request")
    public ResponseEntity acceptRequest(@RequestBody ObjectNode objectNode) {
        try {
            Long id = objectNode.get("id").asLong();
            Request acceptedRequest = waitingList.getRequestById(id);
            if (acceptedRequest != null && (authManager == null || authManager.getRoles().stream()
                    .noneMatch(a -> a.getAuthority().contains("admin_" + acceptedRequest.getFaculty())))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only faculty admins can accept a request!");
            }
            LocalDate currentDate = LocalDate.ofInstant(clock.instant(), clock.getZone());
            acceptedRequest.setPlannedDate(LocalDate.parse(objectNode.get("plannedDate")
                    .asText()), currentDate);
            ResourcesModel resourcesModel = new ResourcesModel(acceptedRequest.getResources().getCpu(),
                    acceptedRequest.getResources().getGpu(), acceptedRequest.getResources().getRam());
            RequestModelSchedule requestModelSchedule = new RequestModelSchedule(acceptedRequest.getId(),
                    acceptedRequest.getName(), acceptedRequest.getDescription(),
                    acceptedRequest.getFaculty(), resourcesModel, acceptedRequest.getPlannedDate());
            if (schedulerService.scheduleRequest(requestModelSchedule).getStatusCode() == HttpStatus.OK) {
                waitingList.removeRequest(id);
                return ResponseEntity.ok().build();
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        } catch (IllegalArgumentException | NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


}
