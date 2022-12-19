package nl.tudelft.sem.template.example.controllers;

import nl.tudelft.sem.common.models.Node;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources")
public class NodeResourceController {
    @PutMapping("/add-node")
    public ResponseEntity<String> addNode(@RequestBody Node node) {
        return ResponseEntity.ok("Here should be response from Request microservice for " + node.toString());
    }
}
