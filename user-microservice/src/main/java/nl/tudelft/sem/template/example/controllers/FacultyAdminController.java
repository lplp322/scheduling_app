package nl.tudelft.sem.template.example.controllers;

import feign.FeignException;
import nl.tudelft.sem.common.models.RequestStatus;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.resources.ReleaseRequestModel;
import nl.tudelft.sem.common.models.response.resources.AvailableResourcesResponseModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.ResourcesInterface;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import nl.tudelft.sem.template.example.acceptrequestadapter.AcceptRequest;
import nl.tudelft.sem.template.example.acceptrequestadapter.AcceptRequestAdapter;
import nl.tudelft.sem.template.example.acceptrequestadapter.AcceptRequestDataModel;
import nl.tudelft.sem.template.example.requests.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/faculty-admin")
public class FacultyAdminController {

    private transient WaitingListInterface waitingListInterface;

    private transient RequestService requestService;

    private transient AuthManager authManager;

    private transient ResourcesInterface resourcesInterface;

    /**
     * Controller for faculty admins.
     *
     * @param waitingListInterface - waitingListFeignClient
     * @param requestService       - service to manage request sdatabase in User
     * @param authManager          - manager for Authentication
     * @param resourcesInterface   - resourcesFeignClient
     */
    @Autowired
    public FacultyAdminController(WaitingListInterface waitingListInterface, RequestService requestService,
                                  AuthManager authManager, ResourcesInterface resourcesInterface) {
        this.waitingListInterface = waitingListInterface;
        this.requestService = requestService;
        this.authManager = authManager;
        this.resourcesInterface = resourcesInterface;
    }

    /**
     * Sends reject-request to waiting list.
     *
     * @param id - id of the request
     * @return response from waiting list
     */
    @DeleteMapping("/reject-request")
    ResponseEntity<String> rejectRequest(@RequestBody Long id) {
        try {
            ResponseEntity<String> waitingListResponse = waitingListInterface.rejectRequest(id);
            if (waitingListResponse.getStatusCode() == HttpStatus.OK) {
                requestService.updateRequestStatus(id, RequestStatus.REJECTED);
            }
            return waitingListResponse;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

    }

    @GetMapping("/get-requests-by-faculty")
    ResponseEntity<String> getRequestsByFaculty(@RequestBody String faculty) {
        try {
            return waitingListInterface.getRequestsByFaculty(faculty);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
    }

    @PostMapping("/accept-request")
    ResponseEntity acceptRequest(@RequestBody AcceptRequestDataModel data) {
        try {
            AcceptRequest acceptRequest = new AcceptRequestAdapter(waitingListInterface);
            ResponseEntity waitingListResponse = acceptRequest.acceptRequest(data);
            if (waitingListResponse.getStatusCode() == HttpStatus.OK) {
                requestService.updateRequestStatus(data.getId(), RequestStatus.ACCEPTED);
            }
            return waitingListResponse;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }


    /**
     * Releasing resources for specific faculty.
     *
     * @param request - DTO to request it
     * @return response from Resources microservice or unauthorized
     */
    @PostMapping("/release-resources")
    public ResponseEntity releaseResources(@RequestBody ReleaseRequestModel request) {
        if (authManager == null || authManager.getRoles().stream()
            .noneMatch(a -> a.getAuthority().contains("admin_" + request.getFaculty()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
        }
        try {
            return resourcesInterface.releaseResources(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
    }

    /**
     * Check for available resources.
     *
     * @param request - DataTransferObject
     * @return available resources
     */
    @GetMapping("/available-resources")
    ResponseEntity<AvailableResourcesResponseModel> getAvailableResources(
        @RequestBody AvailableResourcesRequestModel request) {
        if (authManager == null || authManager.getRoles().stream()
            .noneMatch(a -> a.getAuthority().contains("admin_" + request.getFaculty()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
        }
        try {
            return resourcesInterface.getAvailableResources(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
    }
}
