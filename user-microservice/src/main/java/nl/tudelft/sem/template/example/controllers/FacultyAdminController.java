package nl.tudelft.sem.template.example.controllers;

import feign.FeignException;
import nl.tudelft.sem.common.models.RequestStatus;
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

    @Autowired
    public FacultyAdminController(WaitingListInterface waitingListInterface, RequestService requestService) {
        this.waitingListInterface = waitingListInterface;
        this.requestService = requestService;
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
            ResponseEntity<String> waitingListResponse =  waitingListInterface.rejectRequest(id);
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
            ResponseEntity waitingListResponse =  acceptRequest.acceptRequest(data);
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
}
