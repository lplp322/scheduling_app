package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.common.RequestStatus;
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
     * Approves the request with specific id.
     *
     * @param id - Long - the id of the request that is approved.
     * @return RequestStatus - the status of the request after approval.
     */
    @Override
    public RequestStatus approveRequest(Long id) {
        List<Request> allRequests = requestRepo.findAll();
        List<Request> requestsWithRightId = new ArrayList<>();
        for (int i = 0; i < allRequests.size(); i++) {
            if (allRequests.get(i).getId().equals(id)) {
                requestsWithRightId.add(allRequests.get(i));
            }
        }
        if (requestsWithRightId.size() <= 0) {
            throw new IllegalArgumentException("No request with this ID");
        }
        else if (!(requestsWithRightId.get(0).getStatus().equals(RequestStatus.PENDING))) {
            throw new IllegalArgumentException("Request with this id is not pending");
        }
        else {
            requestsWithRightId.get(0).setStatus(RequestStatus.ACCEPTED);
            return RequestStatus.ACCEPTED;
        }
    }
}
