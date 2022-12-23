package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.resources.PostNodeRequestModel;
import nl.tudelft.sem.common.models.request.resources.ReleaseRequestModel;
import nl.tudelft.sem.template.example.authentication.AuthManager;
import nl.tudelft.sem.template.example.feigninterfaces.ResourcesInterface;
import nl.tudelft.sem.template.example.requests.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/resources")
public class NodeResourceController {

    private final transient AuthManager authManager;

    private final transient ResourcesInterface resourcesInterface;

    @Autowired
    public NodeResourceController(AuthManager authManager, ResourcesInterface resourcesInterface) {
        this.authManager = authManager;
        this.resourcesInterface = resourcesInterface;
    }

    /**
     * Controller to add nodes to resources microservice for regular employees.
     *
     * @param request - request model from commons
     * @return -response from Resources or UNAUTHORIZED
     */
    @PostMapping("/add-node")
    ResponseEntity addNode(@RequestBody PostNodeRequestModel request) {
        try {
            return resourcesInterface.addNode(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e.getCause());
        }
    }
}
