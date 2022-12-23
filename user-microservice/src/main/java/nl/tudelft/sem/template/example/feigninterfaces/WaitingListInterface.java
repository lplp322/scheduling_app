package nl.tudelft.sem.template.example.feigninterfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Service
@FeignClient(name = "waitingListMicroservice", url = "${waitingList.service.url}")
public interface WaitingListInterface {
    @PostMapping("/add-request")
    ResponseEntity<AddResponseModel> addRequest(@RequestBody RequestModelWaitingList requestModel);


    @DeleteMapping("/reject-request")
    ResponseEntity<String> rejectRequest(@RequestBody Long id);

    @PostMapping("/get-requests-by-faculty")
    ResponseEntity<String> getRequestsByFaculty(@RequestBody String faculty);

    @PostMapping("/accept-request")
    ResponseEntity acceptRequest(@RequestBody ObjectNode objectNode);
}
