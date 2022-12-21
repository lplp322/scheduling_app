package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import nl.tudelft.sem.waitinglist.external.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Creates a new waiting list object.
     *
     * @param requestRepo requests repository
     * @param clock clock
     * @param userService user service
     */
    @Autowired
    public SingleTableWaitingList(RequestRepository requestRepo, Clock clock, UserService userService) {
        this.requestRepo = requestRepo;
        this.clock = clock;
        this.userService = userService;
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

    /**
     * Gets a request by id.
     * @param id - request id
     * @return Request - the request with that id.
     */
    @Override
    public Request getRequestById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        if (!requestRepo.existsById(id)) {
            throw new NoSuchElementException("A request with such id does not exist");
        }
        Request request = requestRepo.findById(id).get();
        return request;
    }

    /**
     * Removes a request.
     *
     * @param id request id
     * @throws IllegalArgumentException in case id is null
     * @throws NoSuchElementException in case a request with such id is not in the waiting list
     */
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
}
