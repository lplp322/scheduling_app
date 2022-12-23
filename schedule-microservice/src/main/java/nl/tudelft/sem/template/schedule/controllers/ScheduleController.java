package nl.tudelft.sem.template.schedule.controllers;

import nl.tudelft.sem.common.models.providers.TimeProvider;
import nl.tudelft.sem.common.models.request.DateModel;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.resources.UpdateAvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.response.GetScheduledRequestsResponseModel;
import nl.tudelft.sem.template.schedule.authentication.AuthManager;
import nl.tudelft.sem.template.schedule.domain.request.ScheduleService;
import nl.tudelft.sem.template.schedule.domain.request.ScheduledRequest;
import nl.tudelft.sem.template.schedule.external.ResourcesInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for handling incoming requests regarding scheduling requests and retrieving them from the schedule.
 */
@RestController
public class ScheduleController {

    private final transient AuthManager authManager;

    private final transient ScheduleService scheduleService;

    private final transient TimeProvider timeProvider;

    private final transient ResourcesInterface resourcesInterface;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */
    @Autowired
    public ScheduleController(AuthManager authManager, ScheduleService scheduleService, TimeProvider timeProvider,
                              ResourcesInterface resourcesInterface) {
        this.authManager = authManager;
        this.scheduleService = scheduleService;
        this.timeProvider = timeProvider;
        this.resourcesInterface = resourcesInterface;
    }

    /**
     * Endpoint to retrieve the schedule of a specific date.
     *
     * @param request The date from which the requests should be retrieved.
     * @return The schedule from the specific date.
     */
    @GetMapping("/schedule")
    public ResponseEntity<GetScheduledRequestsResponseModel> getSchedule(@RequestBody DateModel request) {
        try {
            if (authManager == null || authManager.getRoles().stream()
                    .noneMatch(a -> a.getAuthority().contains("sysadmin"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Only sysadmins can view the schedules.");
            }
            List<ScheduledRequest> requests = scheduleService.getSchedule(request.getDate());
            List<RequestModelSchedule> requestModels = new ArrayList<>();
            for (ScheduledRequest scheduledRequest : requests) {
                requestModels.add(scheduledRequest.convert());
            }
            return ResponseEntity.ok(new GetScheduledRequestsResponseModel(request.getDate(), requestModels));
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
    public ResponseEntity scheduleRequest(@RequestBody RequestModelSchedule request) {
        try {
            LocalDate plannedDate = request.getPlannedDate();
            LocalDate currDate = timeProvider.now().toLocalDate();
            LocalTime currTime = timeProvider.now().toLocalTime();
            if (!plannedDate.isAfter(currDate) || (plannedDate.minusDays(1).isEqual(currDate)
                    && currTime.isAfter(LocalTime.of(23, 55)))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You cannot schedule any requests for this date anymore.");
            }

            ResourcesModel requiredResources = request.getResources();
            if (requiredResources.getCpu() < requiredResources.getGpu()
                    || requiredResources.getCpu() < requiredResources.getRam()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You cannot schedule a request requiring more GPU or memory resources than CPU resources");
            }

            ResourcesModel availableResources = resourcesInterface.getAvailableResources(
                    new AvailableResourcesRequestModel(request.getFaculty(), plannedDate)).getBody();
            if (!requiredResources.enoughAvailable(availableResources)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "There are not enough resources available on this date for this request.");
            }
            resourcesInterface.updateAvailableResources(new UpdateAvailableResourcesRequestModel(request.getPlannedDate(),
                    request.getFaculty(), requiredResources.getCpu(), requiredResources.getGpu(),
                    requiredResources.getRam()));
            scheduleService.scheduleRequest(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
