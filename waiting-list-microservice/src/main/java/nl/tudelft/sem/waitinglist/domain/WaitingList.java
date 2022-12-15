package nl.tudelft.sem.waitinglist.domain;

import java.util.NoSuchElementException;

public interface WaitingList {
    /**
     * Adds a request to the waiting list.
     *
     * @param request request to add
     * @return request id
     */
    public Long addRequest(Request request);

    /**
     * Rejects a request.
     *
     * @param id request id
     * @throws IllegalArgumentException in case id is null
     * @throws NoSuchElementException in case a request with such id is not in the waiting list
     */
    public void rejectRequest(Long id);
}
