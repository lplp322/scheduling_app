package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import nl.tudelft.sem.template.example.requestmodelget.RequestModelCreatorStrategy;
import nl.tudelft.sem.template.example.requestmodelget.RequestModelFromJsonStrategy;
import nl.tudelft.sem.template.example.requestmodelget.RequestModelFromXmlStrategy;
import nl.tudelft.sem.template.example.requests.AddNewRequestService;
import nl.tudelft.sem.template.example.requests.RequestService;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class RequestReceivingController {

    private final transient AuthManager authManager;

    private final transient RequestService requestService;

    private final transient AddNewRequestService addNewRequestService;

    /**
     * Instantiates a new controller to recieve user request.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     * @param requestService service to work with request status database
     * @param addNewRequestService service to add new requests and contact Waiting List microservice
     */

    @Autowired
    public RequestReceivingController(AuthManager authManager, RequestService requestService,
                                      AddNewRequestService addNewRequestService) {
        this.authManager = authManager;
        this.requestService = requestService;
        this.addNewRequestService = addNewRequestService;
    }


    /**
     * Gets example by id.
     *
     * @return the example found in the database with the given id
     */
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello " + authManager.getNetId());

    }

    /**
     * Register new request from user.
     *
     * @param httpRequest - new RequestData object
     * @return response to user
     */
    @PostMapping("/request")
    public ResponseEntity<String> addRequest(HttpServletRequest httpRequest) {
        RequestModelCreatorStrategy requestCreator;
        //create proper strategy
        if (httpRequest.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            requestCreator = new RequestModelFromJsonStrategy();
        } else if (httpRequest.getContentType().equals(MediaType.APPLICATION_XML_VALUE)) {
            requestCreator = new RequestModelFromXmlStrategy();
        } else {
            Exception e = new Exception("Unsupported type of body for this request");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return addNewRequestService.createRequest(httpRequest, requestCreator);
    }

    /**
     * Check the status of request.
     *
     * @param id - id of request
     * @return status
     */
    @GetMapping("/request-status")
    public ResponseEntity<RequestStatus> getRequest(@RequestBody Long id) {
        Optional<UserRequest> request = requestService.findById(id);
        if (request.isPresent()) {
            if (authManager == null || !authManager.getNetId().equals(request.get().getUser())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized request access.");
            }
            return ResponseEntity.ok(request.get().getStatus());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct Id");
        }
    }

    /**
     * Get user requests and their status.
     *
     * @return List of requests from User microservice database
     */
    @GetMapping("/get-my-requests")
    public ResponseEntity<List<UserRequest>> getMyRequests() {
        String netId = authManager.getNetId();
        return ResponseEntity.ok(requestService.getAllRequestsByNetId(netId));
    }
}
