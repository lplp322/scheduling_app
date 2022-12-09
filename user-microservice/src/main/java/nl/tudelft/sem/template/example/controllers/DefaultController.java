package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.requests.RequestData;
import nl.tudelft.sem.template.example.requests.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Hello World example controller.
 * <p>
 * This controller shows how you can extract information from the JWT token.
 * </p>
 */
@RestController
public class DefaultController {

    private final transient AuthManager authManager;

    private final transient RequestService requestService;
    /**
     * Instantiates a new controller.
     *
     * @param authManager Spring Security component used to authenticate and authorize the user
     */

    @Autowired
    public DefaultController(AuthManager authManager, RequestService requestService) {
        this.authManager = authManager;
        this.requestService = requestService;
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
     * @param request - new RequestData object
     * @return response to user
     */
    @PostMapping("/request")
    public ResponseEntity<String> addRequest(@RequestBody RequestData request) {
        return ResponseEntity.ok("Your request was created. Request ID: "
            + requestService.saveRequest(request));
    }

}
