package nl.tudelft.sem.resources.controllers;

import nl.tudelft.sem.common.models.request.resources.*;
import nl.tudelft.sem.common.models.request.ResourcesModel;
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
 * Main controller for the Resources microservice.
 */
@RestController
public class ResourcesController {
    private final transient AuthManager authManager;
    private final transient NodeRepositoryService nodeRepositoryService;
    private final transient ResourceRepositoryService resourceRepositoryService;

    /** Constructor.
     *
     * @param authManager Auth manager
     * @param nodeRepositoryService Node repository service
     * @param resourceRepositoryService Resource repository service
     */
    @Autowired
    public ResourcesController(AuthManager authManager, NodeRepositoryService nodeRepositoryService,
                               ResourceRepositoryService resourceRepositoryService) {
        this.authManager = authManager;
        this.nodeRepositoryService = nodeRepositoryService;
        this.resourceRepositoryService = resourceRepositoryService;
    }

    /** Endpoint for adding a node to the cluster.
     *
     * @param request post request.
     * @return 200 OK if node could be added, 400 BAD REQUEST otherwise
     */
    @PostMapping("/nodes")
    public ResponseEntity addNode(@RequestBody PostNodeRequestModel request) {
        Node node = new Node(request.getName(), request.getUrl(), request.getToken(),
                request.getResources(), request.getFaculty());
        try {
            nodeRepositoryService.addNode(node, authManager.getNetId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
        }
        resourceRepositoryService.updateResourceAllocation(node);
        return new ResponseEntity(HttpStatus.OK);
    }

    /** Endpoint for retrieving available resources from the microservice.
     *
     * @param request the get request as described in the model class, it contains a faculty and a date for which
     *                to get available resources.
     * @return the available resources of the given faculty on the given date.
     */
    @GetMapping("/available-resources")
    public ResponseEntity<AvailableResourcesResponseModel> getAvailableResources(
            @RequestBody AvailableResourcesRequestModel request) {
        ResourcesModel resources;
        try {
            resources = resourceRepositoryService.getAvailableResources(request.getFaculty(), request.getDate()); //NOPMD
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Faculty does not exist", e);
        }
        return ResponseEntity.ok(new AvailableResourcesResponseModel(resources));
    }

    /** Endpoint for updating available resources of a faculty on a date.
     *
     * @param request update request as described in the model class
     * @return 200 ok if resources could be updated, 422 unprocessable_entity if requested resources are not available
     */
    @PostMapping("/available-resources")
    public ResponseEntity updateAvailableResources(@RequestBody UpdateAvailableResourcesRequestModel request) {
        if (resourceRepositoryService.updateUsedResources(request.getDate(), request.getFaculty(),
                new ResourcesModel(request.getCpu(), request.getGpu(), request.getRam()))) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Requested resources cannot be subtracted!");
        }
    }

    /** Sysadmin endpoint for retrieving every node in the cluster.
     *
     * @return list of nodes in the cluster
     */
    @GetMapping("/nodes")
    public ResponseEntity<NodesResponseModel> getNodes() {
        return ResponseEntity.ok(new NodesResponseModel(nodeRepositoryService.getAllNodes()));
    }

    /** Endpoint for releasing resources from a faculty in a given time interval.
     *
     * @param request release request object.
     * @return 200 OK if resources could be released 400 BAD REQUEST otherwise.
     */
    @PostMapping("/release")
    public ResponseEntity releaseResources(@RequestBody ReleaseRequestModel request) {
        if (resourceRepositoryService.releaseResources(request.getReleasedResources(), request.getFaculty(),
                request.getFrom(), request.getUntil())) {
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /** Endpoint for releasing every resource on a given day. Implementation is hacky, unsafe and can be improved.
     *
     * @param request release all request object.
     * @return 200 OK if all resources could be released.
     */
    @PostMapping("/release-all")
    public ResponseEntity releaseAllResources(@RequestBody ReleaseAllRequestModel request) {
        resourceRepositoryService.releaseAll(request.getDay());
        return new ResponseEntity(HttpStatus.OK);
    }
}
