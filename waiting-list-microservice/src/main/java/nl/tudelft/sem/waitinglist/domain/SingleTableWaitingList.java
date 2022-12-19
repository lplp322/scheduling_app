package nl.tudelft.sem.waitinglist.domain;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import nl.tudelft.sem.waitinglist.database.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SingleTableWaitingList implements WaitingList {

    private final transient RequestRepository requestRepo;

    @Autowired
    public SingleTableWaitingList(RequestRepository requestRepo) {
        this.requestRepo = requestRepo;
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
}
