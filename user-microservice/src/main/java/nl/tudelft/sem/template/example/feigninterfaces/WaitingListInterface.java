package nl.tudelft.sem.template.example.feigninterfaces;

import nl.tudelft.sem.common.models.request.waitinglist.RequestModel;
import nl.tudelft.sem.common.models.response.waitinglist.AddResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "waitingListMicroservice", url = "http://localhost:8084") //will be not hardcoded later
public interface WaitingListInterface {
    @PostMapping("/add-request")
    ResponseEntity<AddResponseModel> addRequest(@RequestBody RequestModel requestModel);
}
