package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.waitinglist.database.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
     * @return List<Request> - list with all pending requests.
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
     * @return List<Request> - list with all the pending requests the faculty has.
     */

    @Override
    public List<Request> getAllRequestsByFaculty(String faculty) {
        List<Request> result = new ArrayList<>();
        List<Request> requestList = getAllRequests();
        for (int i = 0; i < requestList.size(); i++) {
            if (requestList.get(i).getFaculty().equals(faculty)) {
                result.add(requestList.get(i));
            }
        }
        return result;
    }

}
