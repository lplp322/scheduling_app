package nl.tudelft.sem.resources.feigninterfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@FeignClient(name = "authMicroservice", url = "http://localhost:8081")
public interface AuthenticationInterface {
    @PostMapping(value = "/register")
    ResponseEntity register(@RequestBody RegistrationRequestModel request);

    @PostMapping("/authenticate")
    ResponseEntity<AuthenticationResponseModel> authenticate(@RequestBody AuthenticationRequestModel request);
}
