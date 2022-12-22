package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.requests.RequestService;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/request-status")
public class RequestStatusChangeController {

    private final transient RequestService requestService;
    private final transient AuthManager authManager;


    @Autowired
    public RequestStatusChangeController(AuthManager authManager, RequestService requestService) {
        this.authManager = authManager;
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
        Optional<UserRequest> request = requestService.findById(changeRequestStatus.getId());
        if (request.isPresent() && (authManager == null || authManager.getRoles().stream()
                .noneMatch(a -> a.getAuthority().contains("admin_" + request.get().getFaculty())))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
        }
        try {
            requestService.updateRequestStatus(changeRequestStatus.getId(), changeRequestStatus.getNewStatus());
            return ResponseEntity.ok("Changed");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
