package nl.tudelft.sem.resources.controllers;

import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.resources.PostNodeRequestModel;
import nl.tudelft.sem.common.models.request.resources.UpdateAvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.waitinglist.ResourcesModel;
import nl.tudelft.sem.common.models.response.resources.AvailableResourcesResponseModel;
import nl.tudelft.sem.common.models.response.resources.NodesResponseModel;
import nl.tudelft.sem.resources.authentication.AuthManager;
import nl.tudelft.sem.resources.domain.node.NodeRepositoryService;
import nl.tudelft.sem.resources.domain.resources.ResourceRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import nl.tudelft.sem.common.models.Node;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;

/**
 * Main controller for the Resources microservice
 */
@RestController
public class ResourcesController {
    private final transient AuthManager authManager;
    private final transient Clock clock;
    private final transient NodeRepositoryService nodeRepositoryService;
    private final transient ResourceRepositoryService resourceRepositoryService;

    @Autowired
    public ResourcesController(AuthManager authManager, Clock clock, NodeRepositoryService nodeRepositoryService,
                               ResourceRepositoryService resourceRepositoryService){
        this.authManager = authManager;
        this.clock = clock;
        this.nodeRepositoryService = nodeRepositoryService;
        this.resourceRepositoryService = resourceRepositoryService;
    }

    @PostMapping("/nodes")
    public ResponseEntity addNode(@RequestBody PostNodeRequestModel request){

        try {
            nodeRepositoryService.addNode(new Node(request.getName(), request.getUrl(), request.getToken(),
                    request.getResources(), request.getFaculty()), authManager.getNetId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/available-resources")
    public ResponseEntity<AvailableResourcesResponseModel> getAvailableResources(@RequestBody AvailableResourcesRequestModel request) {
        ResourcesModel resources;
        try {
            resources = resourceRepositoryService.getAvailableResources(request.getFaculty(), request.getDate());
        } catch(Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty does not exist", e);
        }
        return ResponseEntity.ok(new AvailableResourcesResponseModel(resources));
    }

    @PostMapping("/available-resources")
    public ResponseEntity updateAvailableResources(@RequestBody UpdateAvailableResourcesRequestModel request) {
        if(resourceRepositoryService.updateUsedResources(request.getDate(), request.getFaculty(),
                new ResourcesModel(request.getCpu(), request.getGpu(), request.getRam()))){
            return new ResponseEntity(HttpStatus.OK);
        }
        else
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Requested resources cannot be subtracted!");
    }

    //TODO: implement
    @GetMapping("/nodes")
    public ResponseEntity<NodesResponseModel> getNodes() {
        return ResponseEntity.ok(new NodesResponseModel(nodeRepositoryService.getAllNodes()));
    }
}
