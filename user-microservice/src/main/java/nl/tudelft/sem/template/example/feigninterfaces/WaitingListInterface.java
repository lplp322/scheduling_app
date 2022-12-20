package nl.tudelft.sem.template.example.feigninterfaces;

import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "waitingListMicroservice", url = "${waitingList.service.url}")
public interface WaitingListInterface {
    @PostMapping("/add-request")
    ResponseEntity<AddResponseModel> addRequest(@RequestBody RequestModel requestModel);

    //@GetMapping("/get-requests-by-faculty")
    //ResponseEntity<List<Request>> getRequestsByFaculty(@RequestBody String faculty);

    @DeleteMapping("/reject-request")
    ResponseEntity<String> rejectRequest(@RequestBody Long id);
}
