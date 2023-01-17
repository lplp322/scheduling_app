package nl.tudelft.sem.template.example.feigninterfaces;

import nl.tudelft.sem.common.models.Node;
import nl.tudelft.sem.common.models.request.resources.AvailableResourcesRequestModel;
import nl.tudelft.sem.common.models.request.resources.ReleaseRequestModel;
import nl.tudelft.sem.common.models.response.resources.AvailableResourcesResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "resourcesMicroservice", url = "${resources.service.url}")
public interface ResourcesInterface {
    @PostMapping("/nodes")
    ResponseEntity addNode(@RequestBody Node request);

    @PostMapping("/release")
    ResponseEntity releaseResources(@RequestBody ReleaseRequestModel request);

    @PostMapping("/get-available-resources")
    ResponseEntity<AvailableResourcesResponseModel> getAvailableResources(
        @RequestBody AvailableResourcesRequestModel request);
}
