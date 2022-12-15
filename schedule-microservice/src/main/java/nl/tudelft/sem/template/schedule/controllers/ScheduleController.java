package nl.tudelft.sem.template.schedule.controllers;

import nl.tudelft.sem.common.models.providers.TimeProvider;
import nl.tudelft.sem.common.models.request.DateModel;
import nl.tudelft.sem.common.models.request.RequestModel;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.response.GetRequestsResponseModel;
import nl.tudelft.sem.template.schedule.authentication.AuthManager;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class ScheduleController {

    private final transient AuthManager authManager;

    private final transient ScheduleService scheduleService;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public ScheduleController(AuthManager authManager, ScheduleService scheduleService) {
        this.authManager = authManager;
        this.scheduleService = scheduleService;
    }

    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());
    }

    @GetMapping("/schedule")
    public ResponseEntity<GetRequestsResponseModel> getSchedule(@RequestBody DateModel request) {
        //Todo: check date and test
        try {
            List<ScheduledRequest> requests = scheduleService.getSchedule(request.getDate());
            List<RequestModel> requestModels = new ArrayList<>();
            for (ScheduledRequest scheduledRequest : requests) {
                requestModels.add(scheduledRequest.convert());
            }
            return ResponseEntity.ok(new GetRequestsResponseModel(Optional.of(request.getDate()), requestModels));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Post mapping for scheduling a request on a specific day.
     *
     * @param request Object containing the request's information together with the date it should be scheduled on.
     * @return An ok response entity if the request is scheduled, otherwise an exception.
     */
    @PostMapping("/schedule")
    public ResponseEntity scheduleRequest(@RequestBody RequestModel request) {
        try {
            LocalDate date = request.getDeadline();
            if (!date.isAfter(TimeProvider.now())) { //TODO: Check if it's last 5 minutes of day.
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This date has already passed");
            }
            scheduleService.scheduleRequest(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    //TODO: Check for enough resources and if approved, subtract used resources from resources.
}
