package nl.tudelft.sem.template.example.controllers;

import java.time.LocalDate;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.response.resources.AvailableResourcesResponseModel;
import nl.tudelft.sem.template.example.config.GetDate;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.ResourcesInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/resources")
public class NodeResourceController {

    private final transient AuthManager authManager;

    private final transient ResourcesInterface resourcesInterface;

    private final transient GetDate date;

    /**
     * Constructor for controller.
     *
     * @param authManager        - authentication
     * @param resourcesInterface -resources interface
     * @param date               - date provider
     */
    @Autowired
    public NodeResourceController(AuthManager authManager,
                                  ResourcesInterface resourcesInterface, GetDate date) {
        this.authManager = authManager;
        this.resourcesInterface = resourcesInterface;
        this.date = date;
    }

    /**
     * Controller to add nodes to resources microservice for regular employees.
     *
     * @param request - request model from commons
     * @return -response from Resources or UNAUTHORIZED
     */
    @PostMapping("/add-node")
    ResponseEntity addNode(@RequestBody Node request) {
        try {
            return resourcesInterface.addNode(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
    }

    /**
     * Receive resources for tomorrow.
     *
     * @param faculty - faculty that is needed to receive information for
     * @return available resources
     */
    @GetMapping("/get-resources-for-tomorrow")
    ResponseEntity<AvailableResourcesResponseModel> getResourcesForTomorrow(@RequestBody String faculty) {
        if (authManager == null || authManager.getRoles().stream()
            .noneMatch(a -> a.getAuthority().contains("employee_" + faculty))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to make this request!");
        }
        LocalDate now = date.now();
        LocalDate tomorrow = now.plusDays(1);
        AvailableResourcesRequestModel resourcesDoT = new AvailableResourcesRequestModel(faculty, tomorrow);
        try {
            return resourcesInterface.getAvailableResources(resourcesDoT);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
    }
}
