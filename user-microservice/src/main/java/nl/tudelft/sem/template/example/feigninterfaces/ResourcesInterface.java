package nl.tudelft.sem.template.example.feigninterfaces;

import nl.tudelft.sem.common.models.request.resources.PostNodeRequestModel;
import nl.tudelft.sem.common.models.request.resources.ReleaseRequestModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "resourcesMicroservice", url = "${resources.service.url}")
public interface ResourcesInterface {
    @PostMapping("/nodes")
    ResponseEntity addNode(@RequestBody PostNodeRequestModel request);

    @PostMapping("/release")
    public ResponseEntity releaseResources(@RequestBody ReleaseRequestModel request);
}
