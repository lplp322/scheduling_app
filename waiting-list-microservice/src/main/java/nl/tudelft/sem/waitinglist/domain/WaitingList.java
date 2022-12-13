package nl.tudelft.sem.waitinglist.domain;

public interface WaitingList {
    /**
     * Adds a request to the waiting list.
     *
     * @param request request to add
     * @return request id
     */
    public Long addRequest(Request request);
}
