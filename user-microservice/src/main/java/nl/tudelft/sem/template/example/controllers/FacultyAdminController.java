package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.WaitingListInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/faculty-admin/")
public class FacultyAdminController {

    private transient WaitingListInterface waitingListInterface;

    @Autowired
    public FacultyAdminController(WaitingListInterface waitingListInterface) {
        this.waitingListInterface = waitingListInterface;
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
            return waitingListInterface.rejectRequest(id);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }

    }
}
