package nl.tudelft.sem.resources.controllers;

import nl.tudelft.sem.resources.authentication.AuthManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import nl.tudelft.sem.common.models.Node;
import java.time.Clock;

/**
 * Main controller for the Resources microservice
 */
@RestController
public class ResourcesController {
    private final transient AuthManager authManager;
    private final transient Clock clock;

    @Autowired
    public ResourcesController(AuthManager authManager, Clock clock){
        this.authManager = authManager;
        this.clock = clock;
    }

    @PostMapping("/add-node")
    public ResponseEntity addNode(@RequestBody Node node){
        return null;
    }
}
