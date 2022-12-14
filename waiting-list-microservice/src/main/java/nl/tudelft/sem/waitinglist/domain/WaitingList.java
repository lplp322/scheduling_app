package nl.tudelft.sem.waitinglist.domain;

import nl.tudelft.sem.common.RequestStatus;

public interface WaitingList {
    /**
     * Adds a request to the waiting list.
     *
     * @param request request to add
     * @return request id
     */
    public Long addRequest(Request request);

    /**
     * Approves the request with specific id.
     *
     * @param id - Long - the id of the request that is approved.
     * @return RequestStatus - the status of the request after approval.
     */
    public RequestStatus approveRequest(Long id);
}
