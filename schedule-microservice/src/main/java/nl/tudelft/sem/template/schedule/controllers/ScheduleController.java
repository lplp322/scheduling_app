package nl.tudelft.sem.template.schedule.controllers;

import nl.tudelft.sem.template.schedule.authentication.AuthManager;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import nl.tudelft.sem.template.schedule.models.GetScheduleRequestModel;
import nl.tudelft.sem.template.schedule.models.GetScheduleResponseModel;
import nl.tudelft.sem.template.schedule.models.ScheduleRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    public ResponseEntity<GetScheduleResponseModel> getSchedule(@RequestBody GetScheduleRequestModel request) {
        //Todo: check date and test
        try {
            Date date = new Date(request.getYear(), request.getMonth(), request.getDay());
            List<ScheduledRequest> requests = scheduleService.getSchedule(date);
            return ResponseEntity.ok(new GetScheduleResponseModel(date, requests));
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
    public ResponseEntity scheduleRequest(@RequestBody ScheduleRequestModel request) {
        try {
            LocalDate date = LocalDate.of(request.getYear(), request.getMonth(), request.getDay());
            if (!date.isAfter(LocalDate.now())) { //TODO: Check if it's last 5 minutes of day.
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This date has already passed");
            }
            ScheduledRequest newRequest = new ScheduledRequest(request.getName(), request.getDescription(),
                    request.getCpuUsage(), request.getGpuUsage(), request.getMemoryUsage(),
                    date ,request.getNetId());
            scheduleService.scheduleRequest(newRequest);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    //TODO: Check for enough resources and if approved, subtract used resources from resources.
}
