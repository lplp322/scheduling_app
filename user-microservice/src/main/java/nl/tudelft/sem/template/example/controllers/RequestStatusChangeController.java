package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.requests.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/request-status")
public class RequestStatusChangeController {

    private final transient RequestService requestService;


    @Autowired
    public RequestStatusChangeController(RequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * API to change the status of the request.
     *
     * @param changeRequestStatus - DOT from commons module
     * @return "Changed" if it was successful
     */
    @PostMapping("/change-status")
    public ResponseEntity<String> changeRequestStatus(@RequestBody ChangeRequestStatus changeRequestStatus) {
        try {
            requestService.updateRequestStatus(changeRequestStatus.getId(), changeRequestStatus.getNewStatus());
            return ResponseEntity.ok("Changed");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
