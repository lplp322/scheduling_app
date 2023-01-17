package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import nl.tudelft.sem.waitinglist.external.SchedulerService;
import nl.tudelft.sem.waitinglist.external.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class WaitingListAutomaticTasks {

    private final transient RequestRepository requestRepo;
    private final transient Clock clock;
    private final transient UserService userService;
    private final transient SchedulerService schedulerService;

    /**
     * Creates a new waiting list automatic tasks object..
     *
     * @param requestRepo requests repository
     * @param clock clock
     * @param userService user service
     */
    @Autowired
    public WaitingListAutomaticTasks(RequestRepository requestRepo, Clock clock,
                                  UserService userService, SchedulerService schedulerService) {
        this.requestRepo = requestRepo;
        this.clock = clock;
        this.userService = userService;
        this.schedulerService = schedulerService;
    }

    /**
     * Removes pending requests that have a deadline of next day.
     */
    @Scheduled(cron = "0 55 23 * * ?")
    public void removeRequestsForNextDay() {
        LocalDate nextDay = LocalDate.ofInstant(clock.instant(), clock.getZone()).plusDays(1);
        for (Request request : requestRepo.deleteByDeadline(nextDay)) {
            userService.changeRequestStatus(new ChangeRequestStatus(request.getId(), RequestStatus.REJECTED));
        }
    }

    /**
     * In the last 6 hours of each day tries to schedule the pending requests with deadline tomorrow.
     * It will try the request with the lowest id first.
     * If a request can't be scheduled, only smaller requests are tried to schedule.
     */
    @Scheduled(cron = "0 */5 18-23 * * *")

    public void tryToScheduleInLastSixHours() {
        LocalDate tomorrow = LocalDate.ofInstant(clock.instant(), clock.getZone()).plusDays(1);
        List<Request> requestsForTomorrow = requestRepo.getAllRequestsByDeadline(tomorrow);
        Resources resourcesThatAreTooBig = null; //NOPMD
        for (Request request : requestsForTomorrow) {
            LocalDateTime localDateTime = LocalDateTime.ofInstant(clock.instant(), clock.getZone());
            if ((localDateTime.toLocalTime().isBefore(LocalTime.of(23, 55, 0))) && (resourcesThatAreTooBig == null
                    || request.getResources().isResourceSmaller(resourcesThatAreTooBig))) {
                ResourcesModel resourcesModel = new ResourcesModel(request.getResources().getCpu(),
                        request.getResources().getGpu(), request.getResources().getRam());
                RequestModelSchedule requestModelSchedule = new RequestModelSchedule(request.getId(),
                        request.getName(), request.getDescription(),
                        request.getFaculty(), resourcesModel,
                        Request.checkPlannedDate(tomorrow, LocalDate.ofInstant(clock.instant(),
                                clock.getZone()), request.getDeadline()));
                if (schedulerService.scheduleRequest(requestModelSchedule).getStatusCode() == HttpStatus.OK) {
                    requestRepo.deleteById(request.getId());
                    userService.changeRequestStatus(new ChangeRequestStatus(request.getId(), RequestStatus.ACCEPTED));
                } else {
                    resourcesThatAreTooBig = request.getResources(); //NOPMD
                }
            }
        }
    }
}
