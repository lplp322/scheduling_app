package nl.tudelft.sem.template.example.getrequestadapter;

import org.springframework.http.ResponseEntity;

public interface AcceptRequest {
    ResponseEntity acceptRequest(AcceptRequestDataModel data);
}
