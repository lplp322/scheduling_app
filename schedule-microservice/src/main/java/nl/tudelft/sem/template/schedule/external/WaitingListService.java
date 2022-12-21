package nl.tudelft.sem.template.schedule.external;

import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.request.RequestModelWaitingListId;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "waitingListMicroservice", url = "${waitingList.service.url}")
public interface WaitingListService {
    @PostMapping("/re-add-request")
    ResponseEntity addRequest(@RequestBody RequestModelWaitingListId requestModel);
}
