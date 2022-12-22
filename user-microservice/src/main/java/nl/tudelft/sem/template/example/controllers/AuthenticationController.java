package nl.tudelft.sem.template.example.controllers;


import feign.FeignException;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationInterface;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationRequestModel;
import nl.tudelft.sem.template.example.feigninterfaces.AuthenticationResponseModel;
import nl.tudelft.sem.template.example.feigninterfaces.RegistrationRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/login")
public class AuthenticationController {
    @Autowired
    private transient AuthenticationInterface authMicroservice;

    /**
     * Instantiates a new controller.
     */
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegistrationRequestModel request) {
        try {
            return authMicroservice.register(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity authenticate(@RequestBody AuthenticationRequestModel request) {
        try {
            return authMicroservice.authenticate(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
        }
    }
}
