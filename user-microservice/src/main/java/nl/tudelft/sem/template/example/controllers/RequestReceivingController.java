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
import nl.tudelft.sem.template.example.requests.RequestService;
import nl.tudelft.sem.template.example.requests.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        //create proper strategy
        if (httpRequest.getContentType().equals(MediaType.APPLICATION_JSON_VALUE)) {
            requestCreator = new RequestModelFromJsonStrategy();
        } else if (httpRequest.getContentType().equals(MediaType.APPLICATION_XML_VALUE)) {
            requestCreator = new RequestModelFromXmlStrategy();
        } else {
            Exception e = new Exception("Unsupported type of body for this request");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return createRequest(httpRequest, requestCreator);
    }

    private ResponseEntity<String> createRequest(HttpServletRequest httpRequest,
                                                 RequestModelCreatorStrategy requestCreator) {
        try {
            //create request model from httpRequest
            RequestModelWaitingList request = requestCreator.createRequestModel(httpRequest);
            checkAuthorization(request);
            return contactWaitingList(request);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Your request was raising an exception: " + ex.getMessage(), ex);
        }
    }

    private void checkAuthorization(RequestModelWaitingList request) {
        if (authManager == null || !authManager.getNetId().equals(request.getName())
            || authManager.getRoles().stream().noneMatch(a ->
            a.getAuthority().contains("employee_" + request.getFaculty()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to submit this request.");
        }
    }

    private ResponseEntity<String> contactWaitingList(RequestModelWaitingList request) {
        try {
            //contact WaitingList microservice
            ResponseEntity<AddResponseModel> waitingListResponse = waitingListInterface.addRequest(request);
            if (waitingListResponse.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Your request was created. Request ID: "
                    + requestService.saveRequest(request, waitingListResponse.getBody().getId()));
            }
            return ResponseEntity.ok("Your request returned: " + waitingListResponse.getStatusCode()
                + " With body: " + waitingListResponse.getBody());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
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
