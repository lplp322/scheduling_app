package nl.tudelft.sem.template.example.acceptrequestadapter;

import org.springframework.http.ResponseEntity;

public interface AcceptRequest {
    ResponseEntity acceptRequest(AcceptRequestDataModel data);
}
