package nl.tudelft.sem.waitinglist.external;

import nl.tudelft.sem.common.models.request.DateModel;
import nl.tudelft.sem.common.models.request.RequestModelSchedule;
import nl.tudelft.sem.common.models.request.RequestModelWaitingList;
import nl.tudelft.sem.common.models.response.AddResponseModel;
import nl.tudelft.sem.common.models.response.GetScheduledRequestsResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "schedulerMicroservice", url = "${waitingList.service.url}")
public interface SchedulerService {
    @PostMapping("/schedule")
    public ResponseEntity scheduleRequest(@RequestBody RequestModelSchedule request);
}
