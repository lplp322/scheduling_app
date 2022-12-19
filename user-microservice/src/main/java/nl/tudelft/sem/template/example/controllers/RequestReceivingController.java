package nl.tudelft.sem.template.example.controllers;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.common.RequestStatus;
import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import nl.tudelft.sem.template.example.requestmodelget.RequestModelCreatorStrategy;
import nl.tudelft.sem.template.example.requestmodelget.RequestModelFromJsonStrategy;
import nl.tudelft.sem.template.example.requestmodelget.RequestModelFromPlainTextStrategy;
import nl.tudelft.sem.template.example.requests.RequestService;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class RequestReceivingController {

    private final transient AuthManager authManager;

    private final transient RequestService requestService;

    private final transient WaitingListInterface waitingListInterface;

    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */

    @Autowired
    public RequestReceivingController(AuthManager authManager, RequestService requestService,
                                      WaitingListInterface waitingListInterface) {
        this.authManager = authManager;
        this.requestService = requestService;
        this.waitingListInterface = waitingListInterface;
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
        RequestModel request;

        //create proper strategy
        if (httpRequest.getContentType().equals("application/json")) {
            requestCreator = new RequestModelFromJsonStrategy();
        } else if (httpRequest.getContentType().equals("text/plain")) {
            requestCreator = new RequestModelFromPlainTextStrategy();
        } else {
            Exception e = new Exception("Unsupported type of body for this request");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

        //create request with specific strategy
        try {
            request = requestCreator.createRequestModel(httpRequest);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect request model: " + e.getMessage(), e);
        }

        //communication with waiting list
        try {
            ResponseEntity<AddResponseModel> waitingListResponse = waitingListInterface.addRequest(request);
            if (waitingListResponse.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Your request was created. Request ID: "
                    + requestService.saveRequest(request, waitingListResponse.getBody().getId()));
            }
            return ResponseEntity.ok("Your request returned: " + waitingListResponse.getStatusCode()
                + " With body: " + waitingListResponse.getBody());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Your request was raising an exception in waiting list: " + e.getMessage(), e);
        }
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
