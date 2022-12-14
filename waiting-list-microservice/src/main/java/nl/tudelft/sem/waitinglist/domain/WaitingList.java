package nl.tudelft.sem.waitinglist.domain;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WaitingList {
    /**
     * Adds a request to the waiting list.
     *
     * @param request request to add
     * @return request id
     */
    public Long addRequest(Request request);
    /**
     * Gets a list of all the requests in the waiting list.
     *
     * @return List<Request> - list with all pending requests.
     */

    public List<Request> getAllRequests();

    /**
     * Gets a list of all the pending requests a faculty has.
     *
     * @param faculty - String - faculty the list is gotten for
     * @return List<Request> - list with all the pending requests the faculty has.
     */

    public List<Request> getAllRequestsByFaculty(String faculty);

}
