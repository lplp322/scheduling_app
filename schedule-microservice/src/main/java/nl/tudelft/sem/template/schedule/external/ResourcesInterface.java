package nl.tudelft.sem.template.schedule.external;

import nl.tudelft.sem.common.models.request.DateModel;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.ResourcesModel;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "resourcesMicroservice", url = "${resources.service.url}")
public interface ResourcesInterface {
    //TODO: Use real endpoints!
    @GetMapping("/available-resources")
    ResponseEntity<ResourcesModel> getAvailableResources(@RequestBody DateModel request);

    @PostMapping("update-resources")
    ResponseEntity updateUsedResources(@RequestBody ResourcesModel request);
}
