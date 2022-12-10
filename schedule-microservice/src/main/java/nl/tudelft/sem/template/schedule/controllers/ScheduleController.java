package nl.tudelft.sem.template.schedule.controllers;

import nl.tudelft.sem.template.schedule.authentication.AuthManager;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import nl.tudelft.sem.template.schedule.models.ScheduleRequestModel;
import nl.tudelft.sem.template.schedule.models.ScheduleResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ScheduleResponseModel> getSchedule(@RequestBody ScheduleRequestModel request) {
        Date date = request.getDate();
        List<ScheduledRequest> requests = scheduleService.getSchedule(date);
        return ResponseEntity.ok(new ScheduleResponseModel(date, requests));
    }

}
