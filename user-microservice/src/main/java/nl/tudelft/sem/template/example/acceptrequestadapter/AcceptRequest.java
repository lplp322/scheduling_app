package nl.tudelft.sem.template.example.acceptrequestadapter;

import org.springframework.http.ResponseEntity;

/**
 * Interface that is used to accept request.
 */
public interface AcceptRequest {
    /**
     * Accept pending request.
     *
     * @param data - specific data model received from User side
     * @return Response from accepting service
     */
    ResponseEntity acceptRequest(AcceptRequestDataModel data);
}
