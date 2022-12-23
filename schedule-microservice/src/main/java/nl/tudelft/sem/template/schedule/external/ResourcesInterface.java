package nl.tudelft.sem.template.schedule.external;

import nl.tudelft.sem.common.models.request.DateModel;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Profile("!test")
@FeignClient(name = "resourcesMicroservice", url = "${resources.service.url}")
public interface ResourcesInterface {
    //TODO: Use real endpoints!
    @GetMapping("/available-resources")
    ResponseEntity<ResourcesModel> getAvailableResources(@RequestBody DateModel request);

    @PostMapping("available-resources")
    ResponseEntity updateAvailableResources(@RequestBody ResourcesModel request);
}
