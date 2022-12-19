package nl.tudelft.sem.waitinglist.external;

import nl.tudelft.sem.common.models.ChangeRequestStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "userMicroservice", url = "${user.service.url}")
public interface UserService {
    @PostMapping(value = "/request-status/change-status")
    ResponseEntity<String> changeRequestStatus(@RequestBody ChangeRequestStatus changeRequestStatus);
}
