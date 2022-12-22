package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.common.models.ChangeRequestStatus;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.NoSuchElementException;
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
import java.util.List;

@Component
public class SingleTableWaitingList implements WaitingList {

    private final transient RequestRepository requestRepo;
    private final transient Clock clock;
    private final transient UserService userService;
    private final transient SchedulerService schedulerService;

    /**
     * Creates a new waiting list object.
     *
     * @param requestRepo requests repository
     * @param clock clock
     * @param userService user service
     */
    @Autowired
    public SingleTableWaitingList(RequestRepository requestRepo, Clock clock,
                                  UserService userService, SchedulerService schedulerService) {
        this.requestRepo = requestRepo;
        this.clock = clock;
        this.userService = userService;
        this.schedulerService = schedulerService;
    }

    @Override
    public Request getRequestById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!requestRepo.existsById(id)) {
            throw new NoSuchElementException("A request with such id does not exist");
        }
        return requestRepo.getRequestById(id);

    }

    @Override
    public Long addRequest(Request request) {
        // Check that request does not have ID yet
        if (request.getId() != null) {
            throw new IllegalArgumentException("To be added request cannot have an ID");
        }

        Request savedRequest = requestRepo.save(request);
        return savedRequest.getId();
    }

    /**
     * Gets a list of all the requests in the waiting list.
     *
     * @return List of Request - list with all pending requests.
     */

    @Override
    public List<Request> getAllRequests() {
        List<Request> requestList = this.requestRepo.findAll();
        return requestList;
    }

    /**
     * Gets a list of all the pending requests a faculty has.
     *
     * @param faculty - String - faculty the list is gotten for
     * @return List of Request - list with all the pending requests the faculty has.
     */

    @Override
    public List<Request> getAllRequestsByFaculty(String faculty) {
        List<Request> requestList = this.requestRepo.getRequestByFaculty(faculty);
        return requestList;
    }

    @Override
    public void removeRequest(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!requestRepo.existsById(id)) {
            throw new NoSuchElementException("A request with such id does not exist");
        }

        requestRepo.deleteById(id);
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
        for (int i = 0; i < requestsForTomorrow.size(); i++) {
            Request request = requestsForTomorrow.get(i);
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
                    removeRequest(request.getId());
                    userService.changeRequestStatus(new ChangeRequestStatus(request.getId(), RequestStatus.ACCEPTED));
                } else {
                    resourcesThatAreTooBig = request.getResources(); //NOPMD
                }
            }
        }
    }
}
