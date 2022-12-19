package nl.tudelft.sem.waitinglist.domain;

import java.time.LocalDate;
import java.util.NoSuchElementException;
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
     * @return List of Request - list with all pending requests.
     */

    public List<Request> getAllRequests();

    /**
     * Gets a list of all the pending requests a faculty has.
     *
     * @param faculty - String - faculty the list is gotten for
     * @return List of Request - list with all the pending requests the faculty has.
     */

    public List<Request> getAllRequestsByFaculty(String faculty);

    /**
     * Gets a request by id.
     * @param id - request id
     * @return Request - the request with that id.
     */
    public Request getRequestById(Long id);

    /**
     * Removes a request.
     *
     * @param id request id
     * @throws IllegalArgumentException in case id is null
     * @throws NoSuchElementException in case a request with such id is not in the waiting list
     */
    public void removeRequest(Long id);
}
