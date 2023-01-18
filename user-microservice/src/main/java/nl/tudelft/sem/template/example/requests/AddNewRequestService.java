package nl.tudelft.sem.template.example.requests;

import javax.servlet.http.HttpServletRequest;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import nl.tudelft.sem.template.example.requestmodelget.RequestModelCreatorStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AddNewRequestService {
    private final transient WaitingListInterface waitingListInterface;
    private final transient AuthManager authManager;
    private final transient RequestService requestService;

    /**
     * Constructor for the service to add new request.
     *
     * @param waitingListInterface - feigninterface for waiting list
     * @param authManager          - authentication manager
     * @param requestService       - service to store request status to the database
     */
    @Autowired
    public AddNewRequestService(WaitingListInterface waitingListInterface,
                                AuthManager authManager, RequestService requestService) {
        this.waitingListInterface = waitingListInterface;
        this.authManager = authManager;
        this.requestService = requestService;
    }

    /**
     * Method to create request from http request.
     *
     * @param httpRequest    - http request
     * @param requestCreator - strategy to create request
     * @return response to controller
     */
    public ResponseEntity<String> createRequest(HttpServletRequest httpRequest,
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

    /**
     * Private method called by create request to check authorization of the request.
     *
     * @param request - created request model(that can be later sent to WaitingList)
     */
    private void checkAuthorization(RequestModelWaitingList request) throws ResponseStatusException {
        if (authManager == null || !authManager.getNetId().equals(request.getName())
            || authManager.getRoles().stream().noneMatch(a ->
            a.getAuthority().contains("employee_" + request.getFaculty()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to submit this request.");
        }
    }

    /**
     * Private method called by createRequest to contact WaitingList.
     *
     * @param request - request model that will be sent to WaitingList
     * @return response from WaitingList microservice
     */
    private ResponseEntity<String> contactWaitingList(RequestModelWaitingList request) throws ResponseStatusException {
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

}
