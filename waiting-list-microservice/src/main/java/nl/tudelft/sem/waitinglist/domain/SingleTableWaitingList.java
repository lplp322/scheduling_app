package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.waitinglist.database.RequestRepository;
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

    @Autowired
    public SingleTableWaitingList(RequestRepository requestRepo, Clock clock) {
        this.requestRepo = requestRepo;
        this.clock = clock;
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
     * Removes pending requests that have a deadline of next day.
     */
    @Scheduled(cron = "0 55 23 * * ?")
    public void removeRequestsForNextDay() {
        LocalDate nextDay = LocalDate.ofInstant(clock.instant(), clock.getZone()).plusDays(1);
        requestRepo.deleteByDeadline(nextDay);
    }

}
